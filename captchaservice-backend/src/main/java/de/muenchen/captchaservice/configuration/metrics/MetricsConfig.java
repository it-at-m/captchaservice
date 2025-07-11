package de.muenchen.captchaservice.configuration.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter challengeRequestCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter("captcha.controller.challenge.requests");
    }

    @Bean
    public Counter verifyRequestCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter("captcha.controller.verify.requests");
    }
}
