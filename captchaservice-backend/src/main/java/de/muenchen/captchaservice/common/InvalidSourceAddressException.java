package de.muenchen.captchaservice.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidSourceAddressException extends ResponseStatusException {
    public InvalidSourceAddressException(final String sourceAddress) {
        super(HttpStatus.BAD_REQUEST, "Source address " + sourceAddress + " is not valid.");
    }
}
