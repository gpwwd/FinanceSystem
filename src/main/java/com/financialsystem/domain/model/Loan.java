package com.financialsystem.domain.model;

import com.financialsystem.util.LoanConfig;
import com.financialsystem.domain.status.LoanStatus;
import com.financialsystem.domain.strategy.CustomInterestStrategy;
import com.financialsystem.domain.strategy.FixedInterestStrategy;
import com.financialsystem.domain.strategy.InterestCalculationStrategy;
import com.financialsystem.dto.database.LoanDatabaseDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@ToString
@AllArgsConstructor
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Loan {
    private Long id;
    @Getter
    private Long accountId;
    private BigDecimal principalAmount;
    private BigDecimal remainingAmountToPay;
    private BigDecimal interestRate;
    private int termMonths;
    private LocalDateTime createdAt;
    private LoanStatus status;

    private Loan(Long accountId, BigDecimal principalAmount, BigDecimal interestRate,
                int termMonths) {
        this.accountId = accountId;
        this.principalAmount = principalAmount;
        this.remainingAmountToPay = calculateAmountToPayWithInterest(principalAmount, interestRate);
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.createdAt = LocalDateTime.now();
        this.status = LoanStatus.ACTIVE;
    }

    public static Loan createFixedRateLoan(Long accountId, BigDecimal principalAmount, int termMonths) {
        InterestCalculationStrategy interestStrategy = new FixedInterestStrategy();
        BigDecimal interestRate = interestStrategy.calculateInterestRate(principalAmount, termMonths);
        return new Loan(accountId, principalAmount, interestRate, termMonths);
    }

    public static Loan createCustomRateLoan(Long accountId, BigDecimal principalAmount, int termMonths, LoanConfig loanConfig) {
        InterestCalculationStrategy interestStrategy = new CustomInterestStrategy(loanConfig);
        BigDecimal interestRate = interestStrategy.calculateInterestRate(principalAmount, termMonths);
        return new Loan(accountId, principalAmount, interestRate, termMonths);
    }

    private static BigDecimal calculateAmountToPayWithInterest(BigDecimal principal, BigDecimal interestRate) {
        BigDecimal floatPercents = interestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal interest = principal.multiply(floatPercents);
        return principal.add(interest);
    }

    public LoanDatabaseDto toDto() {
        return new LoanDatabaseDto(
                id, accountId, principalAmount, remainingAmountToPay, interestRate, termMonths, createdAt, status);
    }

    public void makePayment(BigDecimal amount) {
        checkStatus(LoanStatus.ACTIVE);
        if (remainingAmountToPay.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Сумма платежа больше остатка кредита");
        }
        remainingAmountToPay = remainingAmountToPay.subtract(amount);
        if(this.isPaidOff()){
            setStatus(LoanStatus.PAID);
        }
    }

    private boolean isPaidOff() {
        return remainingAmountToPay.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGoneOverdue() {
        //boolean termMonthsExpired = LocalDateTime.now().isAfter(createdAt.plusMonths(termMonths));
        boolean termMonthsExpired = LocalDateTime.now().isAfter(createdAt.plusMinutes(termMonths));
        if(!isPaidOff() && termMonthsExpired) {
            log.info("loan expired: {} + time: {}", this, LocalDateTime.now());
            return true;
        }
        log.info("loan not expired: {} + time: {}", this, LocalDateTime.now());
        return false;
    }

    private void checkStatus(LoanStatus status) {
        if (!this.status.equals(status)) {
            throw new IllegalStateException("Deposit status must be " + status + ", actual status is: " + this.status);
        }
    }

    private void setStatus(LoanStatus status) {
        if(this.status != LoanStatus.ACTIVE) {
            return;
        }
        this.status = status;
    }

    public void applyOverduePenalty() {
        setStatus(LoanStatus.OVERDUE);
    }
}
