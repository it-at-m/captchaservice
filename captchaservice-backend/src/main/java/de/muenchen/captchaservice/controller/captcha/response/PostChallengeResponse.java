package de.muenchen.captchaservice.controller.captcha.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.altcha.altcha.Altcha;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostChallengeResponse {

    private Altcha.Challenge challenge;

}
