package de.muenchen.captchaservice.util.networkaddresscalculator;

import lombok.Getter;

public class InvalidAddressException extends RuntimeException {
    @Getter
    private final String address;

    public InvalidAddressException(String message, String address, Exception cause) {
        super(message, cause);
        this.address = address;
    }
}
