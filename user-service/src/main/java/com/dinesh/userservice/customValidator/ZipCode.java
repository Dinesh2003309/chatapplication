package com.dinesh.userservice.customValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ZipCodeValidator.class)
@Documented
public @interface ZipCode {
    String message() default "Invalid ZipCode";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
