package com.financialsystem.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "loan")
@Getter @Setter
public class LoanConfig {
    @Value("${loan.base-rate}")
    private BigDecimal baseRate;

    @Value("${loan.large-loan-threshold}")
    private BigDecimal largeLoanThreshold;

    @Value("${loan.small-loan-threshold}")
    private BigDecimal smallLoanThreshold;

    @Value("${loan.large-loan-discount}")
    private BigDecimal largeLoanDiscount;

    @Value("${loan.small-loan-penalty}")
    private BigDecimal smallLoanPenalty;

    @Value("${loan.long-term-threshold}")
    private int longTermThreshold;

    @Value("${loan.long-term-penalty}")
    private BigDecimal longTermPenalty;

    @Value("${loan.short-term-threshold}")
    private int shortTermThreshold;

    @Value("${loan.short-term-discount}")
    private BigDecimal shortTermDiscount;
}
