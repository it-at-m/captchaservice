package de.muenchen.captchaservice.configuration.captcha;

import java.util.List;

public record CaptchaSite(String siteKey, String secret, List<DifficultyItem> difficultyMap) {
    public CaptchaSite(final String siteKey, final String secret, final List<DifficultyItem> difficultyMap) {
        this.siteKey = siteKey;
        this.secret = secret;
        this.difficultyMap = List.copyOf(difficultyMap);
    }
}
