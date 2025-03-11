package com.financialsystem.dto.database.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDatabaseDto {
    private Long id;
    private AccountStatus status;
    private Long ownerId;
    private Long enterpriseId;
    private Long bankId;
    private Currency currency;
    private LocalDateTime createdAt;
    private BigDecimal balance;
}
