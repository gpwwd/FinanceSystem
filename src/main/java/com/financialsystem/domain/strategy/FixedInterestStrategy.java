package com.financialsystem.domain.strategy;

import com.financialsystem.dto.response.LoanTermDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FixedInterestStrategy implements InterestCalculationStrategy  {
    private static final Map<Integer, BigDecimal> INTEREST_RATES = Map.of(
            3, BigDecimal.valueOf(5),
            6, BigDecimal.valueOf(8),
            12, BigDecimal.valueOf(12),
            24, BigDecimal.valueOf(18)
    );

    @Override
    public BigDecimal calculateInterestRate(BigDecimal principal, int termMonths) {
        BigDecimal interestRate = INTEREST_RATES.get(termMonths);
        if (interestRate == null) {
            throw new IllegalArgumentException("Некорректный срок кредита: " + termMonths);
        }
        return interestRate;
    }

    public static void validateLoanTerm(int termMonths, boolean isFixedInterest) {
        if(!isFixedInterest) {
            return;
        }
        BigDecimal interestRate = INTEREST_RATES.get(termMonths);
        if (interestRate == null) {
            throw new IllegalArgumentException("Некорректный срок кредита: " + termMonths);
        }
    }

    public static List<LoanTermDto> getAllFixedLoanTerms() {
        return INTEREST_RATES.entrySet()
                .stream()
                .map(entry -> new LoanTermDto(entry.getKey(), entry.getValue()))
                .toList();
    }
}
