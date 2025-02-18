package com.financialsystem.domain.model;

import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.dto.AccountDatabaseDto;
import com.financialsystem.dto.DepositDatabseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {
    private Long id;
    @Setter(AccessLevel.PRIVATE)
    private AccountStatus status;
    private Long clientId;
    private BigDecimal balance;

    public static Account create(Long clientId) {
        Account account = new Account();
        account.clientId = clientId;
        account.status = AccountStatus.ACTIVE;
        account.balance = BigDecimal.ZERO;
        return account;
    }

    public AccountDatabaseDto toDto() {
        return new AccountDatabaseDto(
                id, status, clientId, balance);
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
