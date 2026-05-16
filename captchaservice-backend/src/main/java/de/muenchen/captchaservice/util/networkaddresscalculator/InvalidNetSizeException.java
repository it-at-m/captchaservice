package de.muenchen.captchaservice.util.networkaddresscalculator;

import lombok.Getter;

public class InvalidNetSizeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    private final int netSize;

    public InvalidNetSizeException(String message, int netSize) {
        super(message);
        this.netSize = netSize;
    }
}
