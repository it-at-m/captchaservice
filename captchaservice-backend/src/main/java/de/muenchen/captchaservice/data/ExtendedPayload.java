package de.muenchen.captchaservice.data;

import lombok.Getter;
import lombok.Setter;
import org.altcha.altcha.Altcha;

@Getter
@Setter
public class ExtendedPayload extends Altcha.Payload {

    /**
     * The time taken (in milliseconds) to solve the captcha challenge.
     * This field tracks how long it took for the user/client to complete the captcha.
     */
    private Long took;

}
