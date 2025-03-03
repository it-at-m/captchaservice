package de.muenchen.captchaservice.service.siteauth;

import de.muenchen.captchaservice.configuration.captcha.CaptchaProperties;
import de.muenchen.captchaservice.configuration.captcha.CaptchaSite;
import org.springframework.stereotype.Service;

@Service
public class SiteAuthService {

    private final CaptchaProperties captchaProperties;

    public SiteAuthService(final CaptchaProperties captchaProperties) {
        this.captchaProperties = captchaProperties;
    }

    public boolean isAuthorized(final String siteKey, final String siteSecret) {
        final CaptchaSite site = captchaProperties.sites().get(siteKey);
        if (site == null) return false;
        return site.secret().equals(siteSecret);
    }

}
