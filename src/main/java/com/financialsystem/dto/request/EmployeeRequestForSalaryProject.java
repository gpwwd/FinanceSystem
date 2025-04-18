package com.financialsystem.dto.request;

import java.math.BigDecimal;

public record EmployeeRequestForSalaryProject(
        Long salaryProjectId,
        String passportSeriesNumber,
        BigDecimal salaryAmount
) {
}
