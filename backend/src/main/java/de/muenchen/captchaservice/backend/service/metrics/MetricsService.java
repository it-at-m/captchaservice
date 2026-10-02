package de.muenchen.captchaservice.backend.service.metrics;

import de.muenchen.captchaservice.backend.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.backend.util.LogSanitizer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MetricsService {

    public enum CaptchaEventType {
        CHALLENGE_REQUEST,
        VERIFY_SUCCESS,
        VERIFY_FAILURE,
        VERIFY_ERROR
    }

    private final MeterRegistry meterRegistry;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;

    public MetricsService(final MeterRegistry meterRegistry, final InvalidatedPayloadRepository invalidatedPayloadRepository) {
        this.meterRegistry = meterRegistry;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
    }

    /**
     * Records a captcha event using precomputed difficulty context so metrics tagging
     * does not issue additional database queries under load.
     */
    public void recordCaptchaEvent(final String siteKey, final CaptchaEventType eventType, final int difficulty, final boolean isWhitelisted,
            final long sameSourceAddressRequestCount) {
        Counter.builder("captcha.events")
                .tag("site_key", siteKey)
                .tag("difficulty", String.valueOf(difficulty))
                .tag("same_source_address_request_count", String.valueOf(sameSourceAddressRequestCount))
                .tag("is_whitelisted", String.valueOf(isWhitelisted))
                .tag("event_type", eventType.name().toLowerCase(Locale.ROOT))
                .description("All captcha events: challenge requests and verification attempts")
                .register(meterRegistry)
                .increment();
    }

    public void recordClientSolveTime(final String siteKey, final Long solveTime, final int difficulty, final boolean isWhitelisted,
            final long sameSourceAddressRequestCount) {
        if (solveTime == null || solveTime < 0) {
            log.warn("Invalid solve time value: {} for site: {}", solveTime, LogSanitizer.sanitize(siteKey));
            return;
        }

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

    public void initializeInvalidatedPayloadsGauge() {
        Gauge.builder("captcha.invalidated.payloads",
                () -> invalidatedPayloadRepository.countByExpiresAtGreaterThan(Instant.now()))
                .description("Gauge for the number of currently invalidated payloads")
                .register(meterRegistry);
    }

}
