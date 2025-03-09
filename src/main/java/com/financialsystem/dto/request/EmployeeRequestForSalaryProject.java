package com.financialsystem.dto.request;

public record EmployeeRequestForSalaryProject(
        Long salaryProjectId,
        String fullName,
        String passportSeriesNumber
) {
}
