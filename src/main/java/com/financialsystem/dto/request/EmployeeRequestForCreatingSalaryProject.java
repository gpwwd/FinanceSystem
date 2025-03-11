package com.financialsystem.dto.request;

import java.math.BigDecimal;

public record EmployeeRequestForCreatingSalaryProject(
        String passportSeriesNumber,
        BigDecimal salaryAmount
) {
}
