package com.financialsystem.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoanTermValidator.class)
public @interface ValidLoanTerm {
    String message() default "Неверное значение loanTerm. Доступные варианты: 3, 6, 12, 24";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
