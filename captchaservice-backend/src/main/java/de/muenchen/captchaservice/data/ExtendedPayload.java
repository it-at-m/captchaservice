package de.muenchen.captchaservice.data;

import org.altcha.altcha.Altcha;

public class ExtendedPayload extends Altcha.Payload {

    private Long took;

    /**
     * Returns the value of the took field.
     *
     * @return the duration or time value stored in took, or null if not set
     */
    public Long getTook() {
        return took;
    }

    /**
     * Sets the value of the took field.
     *
     * @param took the duration or time value to set
     */
    public void setTook(Long took) {
        this.took = took;
    }

}
