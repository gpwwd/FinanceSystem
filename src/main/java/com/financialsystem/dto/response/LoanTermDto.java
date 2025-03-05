package com.financialsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class LoanTermDto {
    private int months;
    private BigDecimal interestRate;
}
