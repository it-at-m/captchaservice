package de.muenchen.captchaservice.util.networkaddresscalculator;

import lombok.Getter;

public class InvalidAddressException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    private final String address;

    public InvalidAddressException(final String message, final String address, final Exception cause) {
        super(message, cause);
        this.address = address;
    }
}
