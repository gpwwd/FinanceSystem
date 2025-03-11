package com.financialsystem.domain.model.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PersonalAccount extends Account {
    public static Account create(Long ownerId, Long bankId, Currency currency) {
        Account account = new Account();
        account.ownerId = ownerId;
        account.enterpriseId = null;
        account.bankId = bankId;
        account.status = AccountStatus.ACTIVE;
        account.balance = BigDecimal.ZERO;
        account.currency = currency;
        account.createdAt = LocalDateTime.now();
        return account;
    }
}
