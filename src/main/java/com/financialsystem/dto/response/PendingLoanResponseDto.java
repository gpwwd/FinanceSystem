package com.financialsystem.dto.response;

import com.financialsystem.domain.status.PendingEntityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PendingLoanResponseDto {
    private Long id;
    private Long accountId;
    private BigDecimal principalAmount;
    private int termMonths;
    private PendingEntityStatus requestStatus;
    private boolean isFixedInterest;
}
