package com.financialsystem.domain;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Data @AllArgsConstructor @NoArgsConstructor @ToString
public class Deposit {
    private Long id;
    private BigDecimal balance;
    private Long accountId;
    private boolean isBlocked;
    private boolean isFrozen;
    private double annualInterestRate;

    public static Deposit create(Long accountId, double annualInterestRate) {
        Deposit deposit = new Deposit();
        deposit.accountId = accountId;
        deposit.balance = BigDecimal.ZERO;
        deposit.isBlocked = false;
        deposit.isFrozen = false;
        deposit.annualInterestRate = annualInterestRate;
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
    public void addInterest() {
        checkDepositState();
        BigDecimal interest = getBalance().multiply(new BigDecimal(annualInterestRate / 100));
        replenish(interest);
    }

    @Transactional
    public void transfer(BigDecimal amount, Deposit target) {
        this.withdraw(amount);
        target.replenish(amount);
    }

    public void checkDepositState() {
        if (isBlocked) throw new IllegalStateException("Счет заблокирован");
        if (isFrozen) throw new IllegalStateException("Счет заморожен");
    }
}
