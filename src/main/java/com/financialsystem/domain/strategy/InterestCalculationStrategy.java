package com.financialsystem.domain.strategy;

import java.math.BigDecimal;

public interface InterestCalculationStrategy {
    BigDecimal calculateInterestRate(BigDecimal principal, int months);
}
