package de.muenchen.refarch.controller.captcha;

import de.muenchen.refarch.common.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import de.muenchen.refarch.service.captcha.CaptchaService;
import de.muenchen.refarch.service.captcha.SiteAuthService;

@RestController
@RequestMapping("/api/v1/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;
    private final SiteAuthService siteAuthService;

    @PostMapping("/challenge")
    public PostChallengeResponse postChallenge(@RequestBody final PostChallengeRequest request) {
        return new PostChallengeResponse(captchaService.createChallenge());
    }

    @PostMapping("/verify")
    public PostVerifyResponse postVerify(@RequestBody final PostVerifyRequest request) {
        if (!siteAuthService.authorizeSite(request.getSiteKey(), request.getSiteSecret())) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        return new PostVerifyResponse(captchaService.verify(request.getPayload()));
    }
}
