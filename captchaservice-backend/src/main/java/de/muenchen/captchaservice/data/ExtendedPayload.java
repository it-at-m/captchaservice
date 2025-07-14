package de.muenchen.captchaservice.data;

import org.altcha.altcha.Altcha;

public class ExtendedPayload extends Altcha.Payload {

    private Long took;

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

}
