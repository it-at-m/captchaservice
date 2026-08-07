package de.muenchen.captchaservice.backend.service.captcha;

import de.muenchen.captchaservice.backend.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.backend.configuration.captcha.CaptchaSite;
import de.muenchen.captchaservice.backend.data.SourceAddress;
import de.muenchen.captchaservice.backend.entity.InvalidatedPayload;
import de.muenchen.captchaservice.backend.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.backend.service.difficulty.DifficultyService;
import de.muenchen.captchaservice.backend.service.difficulty.SourceAddressDifficulty;
import de.muenchen.captchaservice.backend.service.metrics.MetricsService;
import de.muenchen.captchaservice.backend.service.metrics.MetricsService.CaptchaEventType;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.altcha.altcha.v2.Altcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CaptchaService {

    private final CaptchaProperties captchaProperties;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;
    private final DifficultyService difficultyService;
    private final MetricsService metricsService;

    public CaptchaService(final CaptchaProperties captchaProperties, final DifficultyService difficultyService,
            final InvalidatedPayloadRepository invalidatedPayloadRepository, final MetricsService metricsService) {
        this.captchaProperties = captchaProperties;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
        this.difficultyService = difficultyService;
        this.metricsService = metricsService;

        metricsService.initializeInvalidatedPayloadsGauge();
    }

    public Altcha.Challenge createChallenge(final String siteKey, final SourceAddress sourceAddress) {
        final SourceAddressDifficulty difficultyContext = difficultyService.resolveDifficulty(siteKey, sourceAddress);
        difficultyService.registerRequest(siteKey, sourceAddress);
        // Visit count for metrics includes the request just registered.
        metricsService.recordCaptchaEvent(siteKey, CaptchaEventType.CHALLENGE_REQUEST, difficultyContext.difficulty(), difficultyContext.whitelisted(),
                difficultyContext.visitCount() + 1);
        final Altcha.CreateChallengeOptions options = new Altcha.CreateChallengeOptions()
                .algorithm("SHA-256")
                .hmacSignatureSecret(captchaProperties.hmacKey())
                .cost(difficultyContext.difficulty())
                .expiresInSeconds(captchaProperties.captchaTimeoutSeconds());
        try {
            return Altcha.createChallenge(options);
        } catch (Exception e) {
            log.error("Error creating challenge: {}", e.getMessage());
        }
        return null;
    }

    public boolean verify(final String siteKey, final Altcha.Payload payload, final SourceAddress sourceAddress) {
        if (isPayloadInvalidated(siteKey, payload)) {
            return false;
        }
        try {
            final Altcha.VerifySolutionResult result = Altcha.verifySolution(payload.challenge(), payload.solution(), captchaProperties.hmacKey(),
                    Altcha.kdf("SHA-256"));
            final SourceAddressDifficulty difficultyContext = difficultyService.resolveDifficulty(siteKey, sourceAddress);
            if (result.verified()) {
                metricsService.recordCaptchaEvent(siteKey, CaptchaEventType.VERIFY_SUCCESS, difficultyContext.difficulty(), difficultyContext.whitelisted(),
                        difficultyContext.visitCount());

                if (payload.solution().time() != null) {
                    metricsService.recordClientSolveTime(siteKey, payload.solution().time(), difficultyContext.difficulty(), difficultyContext.whitelisted(),
                            difficultyContext.visitCount());
                }

                invalidatePayload(payload);
            } else {
                metricsService.recordCaptchaEvent(siteKey, CaptchaEventType.VERIFY_FAILURE, difficultyContext.difficulty(), difficultyContext.whitelisted(),
                        difficultyContext.visitCount());
            }
            return result.verified();
        } catch (Exception e) {
            final SourceAddressDifficulty difficultyContext = difficultyService.resolveDifficulty(siteKey, sourceAddress);
            metricsService.recordCaptchaEvent(siteKey, CaptchaEventType.VERIFY_ERROR, difficultyContext.difficulty(), difficultyContext.whitelisted(),
                    difficultyContext.visitCount());
            String payloadHash = "unavailable";
            try {
                payloadHash = getPayloadHash(payload);
            } catch (Exception ignored) {
                // Keep default when hash cannot be computed.
            }
            log.warn("Error verifying captcha payload. payloadHash={}", payloadHash, e);
        }
        return false;
    }

    public void invalidatePayload(final Altcha.Payload payload) {
        final String payloadHash = getPayloadHash(payload);
        final InvalidatedPayload invalidatedPayload = new InvalidatedPayload(payloadHash, Instant.now().plusSeconds(captchaProperties.captchaTimeoutSeconds()));
        invalidatedPayloadRepository.save(invalidatedPayload);
        log.debug("Invalidated payloadHash: {}", payloadHash);
    }

    public boolean isPayloadInvalidated(final String siteKey, final Altcha.Payload payload) {
        final CaptchaSite site = captchaProperties.sites().get(siteKey);
        final String payloadHash = getPayloadHash(payload);
        final Instant now = Instant.now();
        final int maxVerifiesPerPayload = site.maxVerifiesPerPayload();
        if (maxVerifiesPerPayload <= 1) {
            return invalidatedPayloadRepository.existsByPayloadHashAndExpiresAtGreaterThanEqual(payloadHash, now);
        }
        final long payloadHashCount = invalidatedPayloadRepository.countByPayloadHashAndExpiresAtGreaterThanEqual(payloadHash, now);
        return payloadHashCount >= maxVerifiesPerPayload;
    }

    private static String getPayloadHash(final Altcha.Payload payload) {
        return DigestUtils.sha256Hex(payload.challenge().signature());
    }
}
