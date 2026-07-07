package de.muenchen.captchaservice.backend.service.siteauth;

import de.muenchen.captchaservice.backend.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.backend.configuration.captcha.CaptchaSite;
import org.springframework.stereotype.Service;

@Service
public class SiteAuthService {

    private final CaptchaProperties captchaProperties;

    public SiteAuthService(final CaptchaProperties captchaProperties) {
        this.captchaProperties = captchaProperties;
    }

    public boolean isAuthorized(final String siteKey, final String siteSecret) {
        final CaptchaSite site = captchaProperties.sites().get(siteKey);
        return site != null && site.secret().equals(siteSecret);
    }

}
