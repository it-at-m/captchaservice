package de.muenchen.captchaservice.configuration.captcha;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CaptchaSite(String siteKey, String secret, @NotNull List<DifficultyItem> difficultyMap) {
    public CaptchaSite(final String siteKey, final String secret, final List<DifficultyItem> difficultyMap) {
        this.siteKey = siteKey;
        this.secret = secret;
        this.difficultyMap = List.copyOf(difficultyMap);
    }
}
