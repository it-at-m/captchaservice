package de.muenchen.captchaservice.util.networkaddresscalculator;

import lombok.Getter;

public class InvalidNetSizeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    private final int netSize;

    public InvalidNetSizeException(final String message, final int netSize) {
        super(message);
        this.netSize = netSize;
    }
}
