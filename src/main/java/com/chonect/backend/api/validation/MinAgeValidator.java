package com.chonect.backend.api.validation;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {

    private int minAge;

    @Override
    public void initialize(MinAge constraintAnnotation) {
        this.minAge = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            // let @NotNull handle null
            return true;
        }
        LocalDate today = LocalDate.now();
        if (value.isAfter(today)) {
            return false;
        }
        return Period.between(value, today).getYears() >= minAge;
    }
}
