package de.muenchen.captchaservice.validation;

import com.google.common.net.InetAddresses;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SourceAddressConstraintValidator implements ConstraintValidator<ValidSourceAddress, String> {
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext constraintValidatorContext) {
        try {
            InetAddresses.forString(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
