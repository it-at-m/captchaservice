package de.muenchen.captchaservice.controller.captcha.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVerifyResponse {

    private boolean valid;

}
