package com.financialsystem.domain.model.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EnterpriseAccount extends Account {
    public static Account create(Long enterpriseId, Long bankId, Currency currency) {
        Account account = new Account();
        account.enterpriseId = enterpriseId;
        account.ownerId = null;
        account.bankId = bankId;
        account.status = AccountStatus.ACTIVE;
        account.balance = BigDecimal.ZERO;
        account.currency = currency;
        account.createdAt = LocalDateTime.now();
        return account;
    }
}
