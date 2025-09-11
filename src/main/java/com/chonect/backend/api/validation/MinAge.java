package com.chonect.backend.api.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = MinAgeValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface MinAge {
    String message() default "edad mínima no válida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int value();
}
