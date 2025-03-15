package com.financialsystem.dto.response;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.domain.status.PendingEntityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SalaryAccountResponseDto {
    private Long id;
    private AccountStatus status;
    private Long ownerId;
    private Long enterpriseId;
    private Long bankId;
    private Currency currency;
    private LocalDateTime createdAt;
    private BigDecimal balance;

    private Long salaryProjectId;
    private PendingEntityStatus salaryAccountStatus;
    private BigDecimal salaryAmount;
}
