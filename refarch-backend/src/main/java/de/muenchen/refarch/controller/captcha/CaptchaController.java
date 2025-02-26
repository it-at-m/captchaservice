package de.muenchen.refarch.controller.captcha;

import de.muenchen.refarch.common.UnauthorizedException;
import de.muenchen.refarch.controller.captcha.request.PostChallengeRequest;
import de.muenchen.refarch.controller.captcha.request.PostVerifyRequest;
import de.muenchen.refarch.controller.captcha.response.PostChallengeResponse;
import de.muenchen.refarch.controller.captcha.response.PostVerifyResponse;
import de.muenchen.refarch.data.SourceAddress;
import de.muenchen.refarch.service.siteauth.SiteAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.altcha.altcha.Altcha;
import org.springframework.web.bind.annotation.*;
import de.muenchen.refarch.service.captcha.CaptchaService;

@RestController
@RequestMapping("/api/v1/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;
    private final SiteAuthService siteAuthService;

    @PostMapping("/challenge")
    @SneakyThrows
    public PostChallengeResponse postChallenge(@Valid @RequestBody final PostChallengeRequest request) {
        if (!siteAuthService.isAuthorized(request.getSiteKey(), request.getSiteSecret())) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        final SourceAddress sourceAddress = SourceAddress.parse(request.getClientAddress());
        final Altcha.Challenge challenge = captchaService.createChallenge(request.getSiteKey(), sourceAddress);
        return new PostChallengeResponse(challenge);
    }

    @PostMapping("/verify")
    public PostVerifyResponse postVerify(@Valid @RequestBody final PostVerifyRequest request) {
        if (!siteAuthService.isAuthorized(request.getSiteKey(), request.getSiteSecret())) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        final boolean isValid = captchaService.verify(request.getPayload());
        return new PostVerifyResponse(isValid);
    }
}
