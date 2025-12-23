package de.muenchen.captchaservice.configuration.captcha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @param hmacKey
 * @param sites
 * @param captchaTimeoutSeconds How long a created captcha challenge is valid.
 * @param sourceAddressWindowSeconds How long a source address is saved for.
 */
@ConfigurationProperties(prefix = "captcha")
@Validated
public record CaptchaProperties(@NotBlank String hmacKey, Map<String, CaptchaSite> sites, @NotNull Long captchaTimeoutSeconds,
        @NotNull Long sourceAddressWindowSeconds) {
    public CaptchaProperties(final String hmacKey, final Map<String, CaptchaSite> sites, final Long captchaTimeoutSeconds,
            final Long sourceAddressWindowSeconds) {
        this.hmacKey = hmacKey;
        this.sites = sites != null ? Map.copyOf(sites) : Map.of();
        this.captchaTimeoutSeconds = captchaTimeoutSeconds;
        this.sourceAddressWindowSeconds = sourceAddressWindowSeconds;
    }
}
