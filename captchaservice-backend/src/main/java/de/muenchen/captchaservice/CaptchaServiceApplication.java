package de.muenchen.captchaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application class for starting the Captcha Service application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@SuppressWarnings("PMD.UseUtilityClass")
public class CaptchaServiceApplication {
    public static void main(final String[] args) {
        SpringApplication.run(CaptchaServiceApplication.class, args);
    }
}
