package de.muenchen.refarch.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SourceAddressConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSourceAddress {
    String message() default "Invalid source address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
