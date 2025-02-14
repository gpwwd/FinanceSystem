package com.financialsystem.dto;

import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Setter
public class IssueLoanDto {
    private Long accountId;
    private  BigDecimal amount;
    private BigDecimal interestRate;
    private int termMonths;
}
