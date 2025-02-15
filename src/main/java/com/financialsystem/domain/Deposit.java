package com.financialsystem.domain;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


@Data @AllArgsConstructor @NoArgsConstructor @ToString
public class Deposit {
    private Long id;
    private BigDecimal balance;
    private Long accountId;
    private boolean isBlocked;
    private boolean isFrozen;
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
    private LocalDateTime lastInterestDate;
    private int termMonths;

    public static Deposit create(Long accountId, BigDecimal interestRate, int termMonths, BigDecimal principalBalance) {
        Deposit deposit = new Deposit();
        deposit.accountId = accountId;
        deposit.balance = principalBalance;
        deposit.isBlocked = false;
        deposit.isFrozen = false;
        deposit.interestRate = interestRate;
        deposit.createdAt = LocalDateTime.now();
        deposit.lastInterestDate = deposit.createdAt;
        deposit.termMonths = termMonths;
        return deposit;
    }

    public void block() {
        this.isBlocked = true;
    }

    public void freeze() {
        this.isFrozen = true;
    }

    public void unblock() {
        this.isBlocked = false;
    }

    public void unfreeze() {
        this.isFrozen = false;
    }

    @Transactional
    public void withdraw(BigDecimal amount) {
        checkDepositState();
        if (balance.compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        this.balance = balance.subtract(amount);
    }

    @Transactional
    public void replenish(BigDecimal amount) {
        checkDepositState();
        this.balance = balance.add(amount);
    }

    @Transactional
    public void addMonthlyInterest() {
        BigDecimal monthlyInterest = calculateTotalInterest().divide(new BigDecimal(termMonths), RoundingMode.HALF_UP);
        replenish(monthlyInterest);
        lastInterestDate = LocalDateTime.now();
    }

    public boolean isActive() {
        return !isBlocked && !isFrozen;
    }

    @Transactional
    public void transfer(BigDecimal amount, Deposit target) {
        this.withdraw(amount);
        target.replenish(amount);
    }

    public boolean isGoneOverdue() {
        // return LocalDateTime.now().isAfter(createdAt.plusMonths(termMonths));
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(termMonths));
    }

    public boolean isMonthPassed() {
        // return LocalDateTime.now().isAfter(lastInterestDate.plusMonths(1));
        return LocalDateTime.now().isAfter(lastInterestDate.plusMinutes(1));
    }

    private BigDecimal calculateTotalInterest() {
        return getBalance().multiply(interestRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
    }

    private void checkDepositState() {
        if (isBlocked) throw new IllegalStateException("Вклад заблокирован");
        if (isFrozen) throw new IllegalStateException("Вклад заморожен");
    }
}
