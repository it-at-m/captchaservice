package de.muenchen.captchaservice.service.siteauth;

import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import org.springframework.stereotype.Service;

@Service
public class SiteAuthService {

    private final CaptchaProperties captchaProperties;

    public SiteAuthService(final CaptchaProperties captchaProperties) {
        this.captchaProperties = captchaProperties;
    }

    public boolean isAuthorized(final String siteKey, final String siteSecret) {
        return captchaProperties
                .sites()
                .stream()
                .anyMatch(site -> site.siteKey().equals(siteKey) && site.secret().equals(siteSecret));
    }

}
