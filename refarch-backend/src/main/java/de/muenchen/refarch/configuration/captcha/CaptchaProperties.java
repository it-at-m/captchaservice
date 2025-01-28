package de.muenchen.refarch.configuration.captcha;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "captcha")
@Validated
public record CaptchaProperties(@NotBlank String hmacKey, List<CaptchaSite> sites) {
    public CaptchaProperties(final String hmacKey, final List<CaptchaSite> sites) {
        this.hmacKey = hmacKey;
        this.sites = List.copyOf(sites);
    }
}
