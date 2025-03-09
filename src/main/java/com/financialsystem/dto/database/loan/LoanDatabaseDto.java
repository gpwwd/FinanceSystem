package com.financialsystem.dto.database.loan;

import com.financialsystem.domain.status.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LoanDatabaseDto {
    private Long id;
    private Long accountId;
    private BigDecimal principalAmount;
    private BigDecimal remainingAmountToPay;
    private BigDecimal interestRate;
    private int termMonths;
    private LocalDateTime createdAt;
    private LoanStatus status;
}
