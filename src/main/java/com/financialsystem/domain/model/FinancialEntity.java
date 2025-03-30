package com.financialsystem.domain.model;

import java.math.BigDecimal;

public interface FinancialEntity {
    void withdraw(BigDecimal amount);
    void replenish(BigDecimal amount);
    Long getId();
}