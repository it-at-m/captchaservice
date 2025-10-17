package de.muenchen.captchaservice.service.metrics;

import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.repository.CaptchaRequestRepository;
import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.service.difficulty.DifficultyService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class MetricsService {

    public enum VerificationStatus {
        SUCCESS,
        FAILURE,
        ERROR
    }

    private final MeterRegistry meterRegistry;
    private final DifficultyService difficultyService;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;
    private final CaptchaRequestRepository captchaRequestRepository;

    @SuppressFBWarnings(value = { "EI_EXPOSE_REP2" }, justification = "Dependency Injection")
    public MetricsService(MeterRegistry meterRegistry, DifficultyService difficultyService,
            InvalidatedPayloadRepository invalidatedPayloadRepository, CaptchaRequestRepository captchaRequestRepository) {
        this.meterRegistry = meterRegistry;
        this.difficultyService = difficultyService;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
        this.captchaRequestRepository = captchaRequestRepository;
    }

    public void recordChallengeRequest(String siteKey, long difficulty, SourceAddress sourceAddress) {
        final boolean isWhitelisted = difficultyService.isSourceAddressWhitelisted(siteKey, sourceAddress);
        final boolean isWhitelisted = difficultyService.isSourceAddressWhitelisted(siteKey, sourceAddress);
        long sameSourceAddressRequestCount = getSameSourceAddressRequestCount(sourceAddress);

        Counter.builder("captcha.challenge.requests")
                .tag("site_key", siteKey)
                .tag("difficulty", String.valueOf(difficulty))
                .tag("same_source_address_request_count", String.valueOf(sameSourceAddressRequestCount))
                .tag("is_whitelisted", String.valueOf(isWhitelisted))
                .description("Number of captcha challenge requests")
                .register(meterRegistry)
                .increment();
    }

    public void recordVerifyAttempt(String siteKey, SourceAddress sourceAddress, VerificationStatus status) {
        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);
        final boolean isWhitelisted = difficultyService.isSourceAddressWhitelisted(siteKey, sourceAddress);
        long sameSourceAddressRequestCount = getSameSourceAddressRequestCount(sourceAddress);

        Counter.builder("captcha.verify.attempts")
                .tag("site_key", siteKey)
                .tag("difficulty", String.valueOf(difficulty))
                .tag("same_source_address_request_count", String.valueOf(sameSourceAddressRequestCount))
                .tag("is_whitelisted", String.valueOf(isWhitelisted))
                .tag("status", status.name().toLowerCase())
                .description("Number of captcha verification attempts with their outcome status (success, failure, error)")
                .register(meterRegistry)
                .increment();
    }

    public void recordClientSolveTime(String siteKey, SourceAddress sourceAddress, Long solveTime) {
        if (solveTime == null || solveTime < 0) {
            log.warn("Invalid solve time value: {} for site: {}", solveTime, sanitizeForLog(siteKey));
            return;
        }

        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);
        final boolean isWhitelisted = difficultyService.isSourceAddressWhitelisted(siteKey, sourceAddress);
        long sameSourceAddressRequestCount = getSameSourceAddressRequestCount(sourceAddress);

        DistributionSummary.builder("captcha.client.solve.time")
                .tag("site_key", siteKey)
                .tag("difficulty", String.valueOf(difficulty))
                .tag("same_source_address_request_count", String.valueOf(sameSourceAddressRequestCount))
                .tag("is_whitelisted", String.valueOf(isWhitelisted))
                .description("Summary of the time taken by clients to solve captcha challenges")
                .baseUnit("milliseconds")
                .register(meterRegistry)
                .record(solveTime);
    }

    private long getSameSourceAddressRequestCount(SourceAddress sourceAddress) {
        return captchaRequestRepository.countBySourceAddressHashIgnoreCaseAndExpiresAtGreaterThanEqual(sourceAddress.getHash(), Instant.now());
    }

    public void initializeInvalidatedPayloadsGauge() {
        Gauge.builder("captcha.invalidated.payloads",
                () -> invalidatedPayloadRepository.countByExpiresAtGreaterThan(Instant.now()))
                .description("Gauge for the number of currently invalidated payloads")
                .register(meterRegistry);
    }

    /**
     * Sanitizes user input for logging to prevent log injection.
     */
    private static String sanitizeForLog(String input) {
        if (input == null) return null;
        return input.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
