package de.muenchen.captchaservice.util.networkaddresscalculator;

import lombok.Getter;

public class InvalidAddressException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    private final String address;

    public InvalidAddressException(String message, String address, Exception cause) {
        super(message, cause);
        this.address = address;
    }
}
