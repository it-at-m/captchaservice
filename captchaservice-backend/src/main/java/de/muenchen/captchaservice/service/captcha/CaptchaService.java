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
import io.micrometer.core.instrument.Gauge;
import java.util.concurrent.atomic.AtomicLong;
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
    private final AtomicLong invalidatedPayloadCount = new AtomicLong(0);

    @SuppressFBWarnings(value = { "EI_EXPOSE_REP2" }, justification = "Dependency Injection")
    public CaptchaService(final CaptchaProperties captchaProperties, final DifficultyService difficultyService,
            final InvalidatedPayloadRepository invalidatedPayloadRepository, MeterRegistry registry) {
        this.captchaProperties = captchaProperties;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
        this.difficultyService = difficultyService;

        // Initialize counter with current count from database
        this.invalidatedPayloadCount.set(invalidatedPayloadRepository.countByExpiresAtGreaterThan(Instant.now()));

        // Initialize metrics
        this.challengeCounter = Counter.builder("captcha.challenge.requests")
                .description("Counter for captcha challenge requests")
                .register(registry);
        this.verifySuccessCounter = Counter.builder("captcha.verify.success")
                .description("Counter for captcha verify success requests")
                .register(registry);
        this.tookTimeSummary = DistributionSummary.builder("captcha.verify.took.time")
                .description("Summary of the time taken to verify captcha payloads")
                .register(registry);
        Gauge.builder("captcha.invalidated.payloads", invalidatedPayloadCount, AtomicLong::get)
                .description("Gauge for the number of currently invalidated payloads")
                .register(registry);
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
            final boolean isValid = Altcha.verifySolution(payload, captchaProperties.hmacKey(), true);
            if (isValid) {
                if (payload.getTook() != null) {
                    tookTimeSummary.record(payload.getTook());
                }
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
        invalidatedPayloadCount.incrementAndGet();
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

    public void decrementInvalidatedPayloadCount(long count) {
        invalidatedPayloadCount.addAndGet(-count);
    }

    public void resetInvalidatedPayloadCount() {
        this.invalidatedPayloadCount.set(
                invalidatedPayloadRepository.countByExpiresAtGreaterThan(java.time.Instant.now()));
    }
}
