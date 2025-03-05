package com.financialsystem.dto.response;

import com.financialsystem.domain.status.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DepositResponseDto {
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
