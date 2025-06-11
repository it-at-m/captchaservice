package de.muenchen.captchaservice.util.networkaddresscalculator;

import lombok.Getter;

public class InvalidNetSizeException extends RuntimeException {
    @Getter
    private final int netSize;

    public InvalidNetSizeException(String message, int netSize) {
        super(message);
        this.netSize = netSize;
    }
}
