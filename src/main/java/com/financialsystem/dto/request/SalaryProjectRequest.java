package com.financialsystem.dto.request;

import java.util.List;

public record SalaryProjectRequest (
        List<EmployeeRequestForSalaryProject> employees
) {
}
