package com.financialsystem.domain;

import com.financialsystem.dto.DepositDatabseDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


@AllArgsConstructor @NoArgsConstructor @ToString
@Slf4j
public class Deposit {
    private Long id;
    private BigDecimal principalBalance;
    private BigDecimal balance;
    @Getter
    private Long accountId;
    private DepositStatus depositStatus;
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
    private LocalDateTime lastInterestDate;
    private int termMonths;

    public static Deposit create(Long accountId, BigDecimal interestRate, int termMonths, BigDecimal principalBalance) {
        Deposit deposit = new Deposit();
        deposit.accountId = accountId;
        deposit.balance = principalBalance;
        deposit.principalBalance = principalBalance;
        deposit.depositStatus = DepositStatus.ACTIVE;
        deposit.interestRate = interestRate;
        deposit.createdAt = LocalDateTime.now();
        deposit.lastInterestDate = deposit.createdAt;
        deposit.termMonths = termMonths;
        return deposit;
    }

    public DepositDatabseDto toDto() {
        return new DepositDatabseDto(
                id, principalBalance, balance, accountId, depositStatus, interestRate, createdAt, lastInterestDate, termMonths);
    }

    public void setStatus(DepositStatus status) {
        checkStatus(DepositStatus.ACTIVE);
        checkStatus(DepositStatus.FROZEN);
        checkStatus(DepositStatus.BLOCKED);
        this.depositStatus = status;
    }

    public void withdrawInterest(BigDecimal amount) {
        checkStatus(DepositStatus.ACTIVE);
        BigDecimal interest = balance.subtract(principalBalance);
        if (interest.compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно процентов");
        }
        this.balance = balance.subtract(amount);
    }

    private void withdraw(BigDecimal amount) {
        checkStatus(DepositStatus.ACTIVE);
        if (balance.compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        this.balance = balance.subtract(amount);
    }

    @Transactional
    public void replenish(BigDecimal amount) {
        checkStatus(DepositStatus.ACTIVE);
        this.balance = balance.add(amount);
    }

    @Transactional
    public boolean addMonthlyInterestIfRequired() {
        if(!isMonthPassed() || !this.depositStatus.equals(DepositStatus.ACTIVE)){
            log.info(this.toString());
            return false;
        }

        BigDecimal monthlyInterest = calculateTotalInterest().divide(new BigDecimal(12), RoundingMode.HALF_UP);
        replenish(monthlyInterest);
        lastInterestDate = LocalDateTime.now();

        if(isGoneOverdue()){
            this.depositStatus = DepositStatus.COMPLETE;
            log.info("Bonus added: {} and deposit completed ", this);
            return true;
        }

        log.info("Bonus added: {}", this);
        return true;
    }

    public BigDecimal retrieveMoney() {
        checkStatus(DepositStatus.COMPLETE);
        balance = BigDecimal.ZERO;
        depositStatus = DepositStatus.CLOSED;
        return balance;
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
        return this.balance.multiply(interestRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
    }

    private void checkStatus(DepositStatus status) {
        if (!this.depositStatus.equals(status)) {
            throw new IllegalStateException("Deposit status must be " + status + ", actual status is: " + status);
        }
    }
}
