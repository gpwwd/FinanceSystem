package com.financialsystem.domain.strategy;

import com.financialsystem.util.LoanConfig;

import java.math.BigDecimal;

public class CustomInterestStrategy implements InterestCalculationStrategy {

    private final LoanConfig loanConfig;

    public CustomInterestStrategy(LoanConfig loanConfig) {
        this.loanConfig = loanConfig;
    }

    @Override
    public BigDecimal calculateInterestRate(BigDecimal principalAmount, int termMonths) {
        BigDecimal baseRate = loanConfig.getBaseRate();

        if (principalAmount.compareTo(loanConfig.getLargeLoanThreshold()) > 0) {
            baseRate = baseRate.subtract(loanConfig.getLargeLoanDiscount());
        } else if (principalAmount.compareTo(loanConfig.getSmallLoanThreshold()) < 0) {
            baseRate = baseRate.add(loanConfig.getSmallLoanPenalty());
        }

        if (termMonths > loanConfig.getLongTermThreshold()) {
            baseRate = baseRate.add(loanConfig.getLongTermPenalty());
        } else if (termMonths <= loanConfig.getShortTermThreshold()) {
            baseRate = baseRate.subtract(loanConfig.getShortTermDiscount());
        }

        return baseRate;
    }
}
