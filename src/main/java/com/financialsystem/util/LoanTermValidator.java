package com.financialsystem.util;

import com.financialsystem.domain.LoanTerm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoanTermValidator implements ConstraintValidator<ValidLoanTerm, String> {


    @Override
    public void initialize(ValidLoanTerm constraintAnnotation) {
    }

    @Override
    public boolean isValid(String message, ConstraintValidatorContext constraintValidatorContext) {
        if (message == null) {
            return false;
        }

        try {
            LoanTerm.valueOf(message.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
