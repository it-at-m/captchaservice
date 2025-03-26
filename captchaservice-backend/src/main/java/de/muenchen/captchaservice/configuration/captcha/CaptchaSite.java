package de.muenchen.captchaservice.configuration.captcha;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CaptchaSite(String siteKey, String secret, @Min(1) Integer maxVerifiesPerPayload, @NotNull List<DifficultyItem> difficultyMap) {
    public CaptchaSite(final String siteKey, final String secret, final Integer maxVerifiesPerPayload, final List<DifficultyItem> difficultyMap) {
        this.siteKey = siteKey;
        this.secret = secret;
        this.maxVerifiesPerPayload = maxVerifiesPerPayload != null ? maxVerifiesPerPayload : 1;
        this.difficultyMap = List.copyOf(difficultyMap);
    }
}
