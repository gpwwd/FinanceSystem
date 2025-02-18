package com.financialsystem.dto;

import com.financialsystem.domain.status.AccountStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDatabaseDto {
    private Long id;
    private AccountStatus status;
    private Long clientId;
    private BigDecimal balance;
}
