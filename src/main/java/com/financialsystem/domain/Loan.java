package com.financialsystem.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data @ToString
@NoArgsConstructor
public class Loan {
    private Long id;
    private Long accountId;
    private BigDecimal principalAmount;
    private BigDecimal remainingAmountToPay;
    private BigDecimal interestRate;
    private int termMonths;
    private LocalDateTime createdAt;
    private boolean overdue;
    private boolean paid;

    public static Loan createWithCustomInterestRate(Long accountId, BigDecimal principalAmount, int termMonths,
                                                    LoanConfig loanConfig) {
        Loan loan = initializeCommonFields(accountId, principalAmount);
        BigDecimal interestRate = calculateCustomInterestRate(principalAmount, termMonths, loanConfig);
        loan.interestRate = interestRate;
        loan.remainingAmountToPay = calculateAmountToPayWithInterest(principalAmount, interestRate);
        loan.termMonths = termMonths;
        return loan;
    }

    public static Loan createWithFixedInterestRate(Long accountId, BigDecimal principalAmount, LoanTerm loanTerm) {
        Loan loan = initializeCommonFields(accountId, principalAmount);
        loan.remainingAmountToPay = calculateAmountToPayWithInterest(principalAmount, loanTerm.getInterestRate());
        loan.interestRate = loanTerm.getInterestRate();
        loan.termMonths = loanTerm.getTermMonths();
        return loan;
    }

    private static Loan initializeCommonFields(Long accountId, BigDecimal principalAmount) {
        Loan loan = new Loan();
        loan.accountId = accountId;
        loan.principalAmount = principalAmount;
        loan.createdAt = LocalDateTime.now();
        loan.overdue = false;
        loan.paid = false;
        return loan;
    }

    private static BigDecimal calculateCustomInterestRate(BigDecimal principalAmount, int termMonths, LoanConfig loanConfig){
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

    private static BigDecimal calculateAmountToPayWithInterest(BigDecimal principal, BigDecimal interestRate) {
        BigDecimal floatPercents = interestRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal interest = principal.multiply(floatPercents);
        return principal.add(interest);
    }

    public void makePayment(BigDecimal amount) {
        if (remainingAmountToPay.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Сумма платежа больше остатка кредита");
        }
        remainingAmountToPay = remainingAmountToPay.subtract(amount);
    }

    public boolean isPaidOff() {
        return remainingAmountToPay.compareTo(BigDecimal.ZERO) == 0;
    }

    public void markAsPaidOff() {
        this.paid = true;
    }

    private boolean isGoneOverdue() {
        return LocalDateTime.now().isAfter(createdAt.plusMonths(termMonths));
    }

    public void applyOverdue() {
        if(isGoneOverdue()) {
            this.overdue = true;
        }
    }
}
