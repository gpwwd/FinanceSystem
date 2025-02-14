package com.financialsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class Account {
    private Long id;
    private List<Deposit> deposits;
    private List<Loan> loans;
    private boolean isBlocked;
    private boolean isFrozen; // добавить в бд
    private Long clientId;
    private BigDecimal balance;// добавить в бд

    public static Account create(Long clientId) {
        Account account = new Account();
        account.deposits = new ArrayList<Deposit>();
        account.loans = new ArrayList<Loan>();
        account.clientId = clientId;
        account.isBlocked = false;
        account.isFrozen = false;
        return account;
    }

    public void block() {
        this.isBlocked = true;
        for(var deposit : deposits) {
            deposit.block();
        }
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

    public void checkAccountState() {
        if (isBlocked) throw new IllegalStateException("Счет заблокирован");
        if (isFrozen) throw new IllegalStateException("Счет заморожен");
    }
}
