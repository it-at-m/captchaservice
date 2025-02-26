package de.muenchen.refarch.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SourceAddressConstraintValidator implements ConstraintValidator<ValidSourceAddress, String> {
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext constraintValidatorContext) {
        try {
            InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            return false;
        }
        return true;
    }
}
