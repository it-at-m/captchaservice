package de.muenchen.captchaservice.configuration.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    /**
     * Registers a Micrometer counter to track the number of captcha challenge requests.
     *
     * @param meterRegistry the Micrometer registry used to create the counter
     * @return a Counter for monitoring captcha challenge request counts
     */
    @Bean
    public Counter challengeRequestCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter("captcha.controller.challenge.requests");
    }

    /**
     * Registers a Micrometer counter to track the number of captcha verification requests.
     *
     * @return a Counter for monitoring verification endpoint requests
     */
    @Bean
    public Counter verifyRequestCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter("captcha.controller.verify.requests");
    }
}
