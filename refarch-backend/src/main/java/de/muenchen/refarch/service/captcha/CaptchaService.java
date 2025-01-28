package de.muenchen.refarch.service.captcha;

import de.muenchen.refarch.configuration.captcha.CaptchaProperties;
import lombok.extern.slf4j.Slf4j;
import org.altcha.altcha.Altcha;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CaptchaService {

    private final CaptchaProperties captchaProperties;

    public CaptchaService(final CaptchaProperties captchaProperties) {
        this.captchaProperties = captchaProperties;
    }

    public Altcha.Challenge createChallenge() {
        final Altcha.ChallengeOptions options = new Altcha.ChallengeOptions();
        options.algorithm = Altcha.Algorithm.SHA256;
        options.hmacKey = captchaProperties.hmacKey();
        try {
            return Altcha.createChallenge(options);
        } catch (Exception e) {
            log.error("Error creating challenge: {}", e.getMessage());
        }
        return null;
    }

    public boolean verify(final String payload) {
        try {
            return Altcha.verifySolution(payload, captchaProperties.hmacKey(), true);
        } catch (Exception e) {
            log.warn("Error verifying captcha payload: {}", payload, e);
        }
        return false;
    }

}
