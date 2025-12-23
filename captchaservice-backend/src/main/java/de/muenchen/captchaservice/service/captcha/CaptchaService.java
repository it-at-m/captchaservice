package de.muenchen.captchaservice.service.captcha;

import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.configuration.captcha.CaptchaSite;
import de.muenchen.captchaservice.data.ExtendedPayload;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.entity.InvalidatedPayload;
import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.service.difficulty.DifficultyService;
import de.muenchen.captchaservice.service.metrics.MetricsService;
import de.muenchen.captchaservice.service.metrics.MetricsService.CaptchaEventType;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.altcha.altcha.Altcha;
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
            final InvalidatedPayloadRepository invalidatedPayloadRepository, MetricsService metricsService) {
        this.captchaProperties = captchaProperties;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
        this.difficultyService = difficultyService;
        this.metricsService = metricsService;

        metricsService.initializeInvalidatedPayloadsGauge();
    }

    public Altcha.Challenge createChallenge(final String siteKey, final SourceAddress sourceAddress) {
        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);
        difficultyService.registerRequest(siteKey, sourceAddress);
        metricsService.recordCaptchaEvent(siteKey, sourceAddress, CaptchaEventType.CHALLENGE_REQUEST);
        final Altcha.ChallengeOptions options = new Altcha.ChallengeOptions();
        options.algorithm = Altcha.Algorithm.SHA256;
        options.hmacKey = captchaProperties.hmacKey();
        options.maxNumber = difficulty;
        options.expires = (System.currentTimeMillis() / 1000) + captchaProperties.captchaTimeoutSeconds();
        try {
            return Altcha.createChallenge(options);
        } catch (Exception e) {
            log.error("Error creating challenge: {}", e.getMessage());
        }
        return null;
    }

    public boolean verify(final String siteKey, final ExtendedPayload payload, final SourceAddress sourceAddress) {
        if (isPayloadInvalidated(siteKey, payload)) {
            return false;
        }
        try {
            Altcha.Payload base = payload.toBasePayload();
            final boolean isValid = Altcha.verifySolution(base, captchaProperties.hmacKey(), true);
            if (isValid) {
                metricsService.recordCaptchaEvent(siteKey, sourceAddress, CaptchaEventType.VERIFY_SUCCESS);

                if (payload.getTook() != null) {
                    metricsService.recordClientSolveTime(siteKey, sourceAddress, payload.getTook());
                }

                invalidatePayload(payload);
            } else {
                metricsService.recordCaptchaEvent(siteKey, sourceAddress, CaptchaEventType.VERIFY_FAILURE);
            }
            return isValid;
        } catch (Exception e) {
            metricsService.recordCaptchaEvent(siteKey, sourceAddress, CaptchaEventType.VERIFY_ERROR);
            log.warn("Error verifying captcha payload: {}", payload, e);
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
        final long payloadHashCount = invalidatedPayloadRepository.countByPayloadHashIgnoreCaseAndExpiresAtGreaterThanEqual(payloadHash, Instant.now());
        return payloadHashCount >= site.maxVerifiesPerPayload();
    }

    private static String getPayloadHash(final Altcha.Payload payload) {
        return DigestUtils.sha256Hex(String.format(
                "%s,%s,%d,%s,%s",
                payload.algorithm,
                payload.challenge,
                payload.number,
                payload.salt,
                payload.signature));
    }
}
