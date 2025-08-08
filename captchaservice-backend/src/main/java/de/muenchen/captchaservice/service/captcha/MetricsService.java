package de.muenchen.captchaservice.service.captcha;

import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.repository.InvalidatedPayloadRepository;
import de.muenchen.captchaservice.service.difficulty.DifficultyService;
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

    private final MeterRegistry meterRegistry;
    private final DifficultyService difficultyService;
    private final InvalidatedPayloadRepository invalidatedPayloadRepository;

    public MetricsService(MeterRegistry meterRegistry, DifficultyService difficultyService,
            InvalidatedPayloadRepository invalidatedPayloadRepository) {
        this.meterRegistry = meterRegistry;
        this.difficultyService = difficultyService;
        this.invalidatedPayloadRepository = invalidatedPayloadRepository;
    }

    public void recordChallengeRequest(String siteKey, SourceAddress sourceAddress) {
        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);

        Counter.builder("captcha.challenge.requests")
                .tag("site_key", siteKey)
                .tag("difficulty", String.valueOf(difficulty))
                .description("Counter for captcha challenge requests")
                .register(meterRegistry)
                .increment();
    }

    public void recordVerifySuccess(String siteKey, SourceAddress sourceAddress) {
        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);

        Counter.builder("captcha.verify.success")
                .tag("site_key", siteKey)
                .tag("difficulty", String.valueOf(difficulty))
                .description("Counter for captcha verify success requests")
                .register(meterRegistry)
                .increment();
    }

    public void recordClientSolveTime(String siteKey, SourceAddress sourceAddress, long solveTime) {
        if (solveTime <= 0) {
            log.warn("Invalid solve time value: {} for site: {}", solveTime, siteKey);
            return;
        }

        final long difficulty = difficultyService.getDifficultyForSourceAddress(siteKey, sourceAddress);

        DistributionSummary.builder("captcha.client.solve.time")
                .tag("site_key", siteKey)
                .tag("difficulty", String.valueOf(difficulty))
                .description("Summary of the time taken by clients to solve captcha challenges")
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
