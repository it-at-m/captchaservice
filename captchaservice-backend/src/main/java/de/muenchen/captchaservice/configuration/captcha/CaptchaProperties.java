package de.muenchen.captchaservice.configuration.captcha;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties(prefix = "captcha")
@Validated
public record CaptchaProperties(@NotBlank String hmacKey, long ttlSeconds, Map<String, CaptchaSite> sites, long sourceAddressWindowSeconds) {
    public CaptchaProperties(final String hmacKey, final long ttlSeconds, final Map<String, CaptchaSite> sites, final long sourceAddressWindowSeconds) {
        this.hmacKey = hmacKey;
        this.ttlSeconds = ttlSeconds;
        this.sites = Map.copyOf(sites);
        this.sourceAddressWindowSeconds = sourceAddressWindowSeconds;
    }
}
