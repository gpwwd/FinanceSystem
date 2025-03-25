package com.financialsystem.domain.model.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EnterpriseAccount extends Account {
    public static EnterpriseAccount create(Long enterpriseId, Long bankId, Currency currency) {
        EnterpriseAccount account = new EnterpriseAccount();
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
