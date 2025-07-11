package de.muenchen.captchaservice.service.captcha;

import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.configuration.captcha.CaptchaSite;
import de.muenchen.captchaservice.data.ExtendedPayload;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.entity.InvalidatedPayload;
import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.service.difficulty.DifficultyService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import lombok.extern.slf4j.Slf4j;
import org.altcha.altcha.Altcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class CaptchaService {

    private final CaptchaProperties captchaProperties;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;
    private final DifficultyService difficultyService;
    private final Counter challengeCounter;
    private final Counter verifySuccessCounter;
    private final DistributionSummary tookTimeSummary;

    @SuppressFBWarnings(value = { "EI_EXPOSE_REP2" }, justification = "Dependency Injection")
    public CaptchaService(final CaptchaProperties captchaProperties, final DifficultyService difficultyService,
            final InvalidatedPayloadRepository invalidatedPayloadRepository, MeterRegistry meterRegistry) {
        this.captchaProperties = captchaProperties;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
        this.difficultyService = difficultyService;

        // Initialize metrics
        this.challengeCounter = meterRegistry.counter("captcha.challenge.requests");
        this.verifySuccessCounter = meterRegistry.counter("captcha.verify.success");
        this.tookTimeSummary = meterRegistry.summary("captcha.verify.took.time");
    }

    public Altcha.Challenge createChallenge(final String siteKey, final SourceAddress sourceAddress) {
        challengeCounter.increment();
        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);
        difficultyService.registerRequest(siteKey, sourceAddress);
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

    public boolean verify(final String siteKey, final ExtendedPayload payload) {
        if (isPayloadInvalidated(siteKey, payload)) {
            return false;
        }
        try {
            if (payload.getTook() != null) {
                tookTimeSummary.record(payload.getTook());
            }
            
            final boolean isValid = Altcha.verifySolution(payload, captchaProperties.hmacKey(), true);
            if (isValid) {
                verifySuccessCounter.increment();
                invalidatePayload(payload);
            }
            return isValid;
        } catch (Exception e) {
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
