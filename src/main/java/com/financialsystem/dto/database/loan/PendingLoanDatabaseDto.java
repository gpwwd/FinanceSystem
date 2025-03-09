package com.financialsystem.dto.database.loan;

import com.financialsystem.domain.status.PendingEntityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PendingLoanDatabaseDto {
    private Long id;
    private Long accountId;
    private BigDecimal principalAmount;
    private int termMonths;
    private PendingEntityStatus requestStatus;
    private boolean isFixedInterest;

    public PendingLoanDatabaseDto(Long accountId, BigDecimal amount,
                                  int termMonths, boolean isFixedInterest) {
        this.accountId = accountId;
        this.principalAmount = amount;
        this.termMonths = termMonths;
        this.isFixedInterest = isFixedInterest;
        this.requestStatus = PendingEntityStatus.PENDING;
    }
}
