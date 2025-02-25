package com.financialsystem.dto.database;

import com.financialsystem.domain.status.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDatabaseDto {
    private Long id;
    private AccountStatus status;
    private Long clientId;
    private BigDecimal balance;
}
