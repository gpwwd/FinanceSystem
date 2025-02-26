package com.financialsystem.dto.database;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountDatabaseDto {
    private Long id;
    private AccountStatus status;
    private Long ownerId;
    private Long bankId;
    private Currency currency;
    private LocalDateTime createdAt;
    private boolean isSalary;
    private BigDecimal balance;
}
