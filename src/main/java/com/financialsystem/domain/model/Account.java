package com.financialsystem.domain.model;

import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.dto.database.AccountDatabaseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {
    private Long id;
    @Setter(AccessLevel.PRIVATE)
    private AccountStatus status;
    private Long ownerId;
    private Long bankId;
    private Currency currency;
    private LocalDateTime createdAt;
    private boolean isSalary;
    private BigDecimal balance;

    public static Account create(Long ownerId, Long bankId, Currency currency, boolean isSalary) {
        Account account = new Account();
        account.ownerId = ownerId;
        account.bankId = bankId;
        account.status = AccountStatus.ACTIVE;
        account.balance = BigDecimal.ZERO;
        account.currency = currency;
        account.createdAt = LocalDateTime.now();
        account.isSalary = isSalary;
        return account;
    }

    public AccountDatabaseDto toDto() {
        return new AccountDatabaseDto(
                id, status, ownerId, bankId, currency, createdAt, isSalary, balance);
    }

    public void block() {
        setStatus(AccountStatus.BLOCKED);
    }

    @Transactional
    public void withdraw(BigDecimal amount) {
        checkAccountState();
        if (balance.compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }
        this.balance = balance.subtract(amount);
    }

    @Transactional
    public void replenish(BigDecimal amount) {
        checkAccountState();
        this.balance = balance.add(amount);
    }

    private void checkAccountState() {
        if(status == AccountStatus.BLOCKED || status == AccountStatus.FROZEN) {
            throw new IllegalStateException("Account status must be " + status + ", actual status is: " + this.status);
        }
    }
}
