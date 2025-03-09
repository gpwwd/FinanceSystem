package com.financialsystem.domain.model.deposit;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DepositTerm {
    THREE_MONTHS(3, new BigDecimal("3.5")),
    SIX_MONTHS(6, new BigDecimal("4.2")),
    TWELVE_MONTHS(12, new BigDecimal("5.0")),
    TWENTY_FOUR_MONTHS(24, new BigDecimal("5.8"));

    private final int months;
    private final BigDecimal interestRate;

    public static DepositTerm fromMonths(int termMonths) {
        return Arrays.stream(values())
                .filter(term -> term.getMonths() == termMonths)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неверный срок депозита: " + termMonths));
    }
}
