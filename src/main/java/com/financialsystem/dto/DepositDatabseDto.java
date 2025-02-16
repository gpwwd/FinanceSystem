package com.financialsystem.dto;

import com.financialsystem.domain.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DepositDatabseDto {
    private Long id;
    private BigDecimal principalBalance;
    private BigDecimal balance;
    private Long accountId;
    private DepositStatus depositStatus;
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
    private LocalDateTime lastInterestDate;
    private int termMonths;
}
