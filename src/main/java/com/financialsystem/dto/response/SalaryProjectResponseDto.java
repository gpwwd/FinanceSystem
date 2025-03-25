package com.financialsystem.dto.response;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.SalaryProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SalaryProjectResponseDto {
    private Long id;
    private Long enterpriseId;
    private Long bankId;
    private Currency currency;
    private LocalDateTime createdAt;
    private SalaryProjectStatus status;
}
