package de.muenchen.captchaservice.controller.captcha;

import de.muenchen.captchaservice.common.UnauthorizedException;
import de.muenchen.captchaservice.controller.captcha.request.PostChallengeRequest;
import de.muenchen.captchaservice.controller.captcha.request.PostVerifyRequest;
import de.muenchen.captchaservice.controller.captcha.response.PostChallengeResponse;
import de.muenchen.captchaservice.controller.captcha.response.PostVerifyResponse;
import de.muenchen.captchaservice.data.SourceAddress;
import de.muenchen.captchaservice.service.captcha.CaptchaService;
import de.muenchen.captchaservice.service.siteauth.SiteAuthService;
import de.muenchen.captchaservice.service.sourceaddress.SourceAddressService;
import io.micrometer.core.instrument.Counter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.altcha.altcha.Altcha;
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
    private final Counter challengeRequestCounter;
    private final Counter verifyRequestCounter;

    /**
     * Handles a POST request to generate a new CAPTCHA challenge for an authorized site.
     *
     * Validates the request and checks site credentials. If authorized, increments the challenge request counter,
     * parses the client's source address, creates a CAPTCHA challenge, and returns the challenge response.
     *
     * @param request the request containing site credentials and client address
     * @return a response containing the generated CAPTCHA challenge
     * @throws UnauthorizedException if the provided site credentials are invalid
     */
    @PostMapping("/challenge")
    @SneakyThrows
    public PostChallengeResponse postChallenge(@Valid @RequestBody final PostChallengeRequest request) {
        if (!siteAuthService.isAuthorized(request.getSiteKey(), request.getSiteSecret())) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        challengeRequestCounter.increment();
        final SourceAddress sourceAddress = sourceAddressService.parse(request.getSiteKey(), request.getClientAddress());
        final Altcha.Challenge challenge = captchaService.createChallenge(request.getSiteKey(), sourceAddress);
        return new PostChallengeResponse(challenge);
    }

    /**
     * Verifies a CAPTCHA response for a given site key and payload.
     *
     * @param request the verification request containing the site key, site secret, and CAPTCHA payload
     * @return a response indicating whether the CAPTCHA verification was successful
     * @throws UnauthorizedException if the provided site credentials are invalid
     */
    @PostMapping("/verify")
    public PostVerifyResponse postVerify(@Valid @RequestBody final PostVerifyRequest request) {
        if (!siteAuthService.isAuthorized(request.getSiteKey(), request.getSiteSecret())) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        verifyRequestCounter.increment();
        final boolean isValid = captchaService.verify(request.getSiteKey(), request.getPayload());
        return new PostVerifyResponse(isValid);
    }
}
