package de.muenchen.captchaservice.backend.controller.captcha;

import de.muenchen.captchaservice.backend.common.UnauthorizedException;
import de.muenchen.captchaservice.backend.controller.captcha.request.PostChallengeRequest;
import de.muenchen.captchaservice.backend.controller.captcha.request.PostVerifyRequest;
import de.muenchen.captchaservice.backend.controller.captcha.response.PostChallengeResponse;
import de.muenchen.captchaservice.backend.controller.captcha.response.PostVerifyResponse;
import de.muenchen.captchaservice.backend.data.SourceAddress;
import de.muenchen.captchaservice.backend.service.captcha.CaptchaService;
import de.muenchen.captchaservice.backend.service.siteauth.SiteAuthService;
import de.muenchen.captchaservice.backend.service.sourceaddress.SourceAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.altcha.altcha.v2.Altcha;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;
    private final SiteAuthService siteAuthService;
    private final SourceAddressService sourceAddressService;

    @PostMapping("/challenge")
    @SneakyThrows
    public PostChallengeResponse postChallenge(@Valid @RequestBody final PostChallengeRequest request) {
        if (!siteAuthService.isAuthorized(request.getSiteKey(), request.getSiteSecret())) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        final SourceAddress sourceAddress = sourceAddressService.parse(request.getSiteKey(), request.getClientAddress());
        final Altcha.Challenge challenge = captchaService.createChallenge(request.getSiteKey(), sourceAddress);
        return new PostChallengeResponse(challenge);
    }

    @PostMapping("/verify")
    public PostVerifyResponse postVerify(@Valid @RequestBody final PostVerifyRequest request) {
        if (!siteAuthService.isAuthorized(request.getSiteKey(), request.getSiteSecret())) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        final SourceAddress sourceAddress = sourceAddressService.parse(request.getSiteKey(), request.getClientAddress());
        final boolean isValid = captchaService.verify(request.getSiteKey(), request.getPayload(), sourceAddress);
        return new PostVerifyResponse(isValid);
    }
}
