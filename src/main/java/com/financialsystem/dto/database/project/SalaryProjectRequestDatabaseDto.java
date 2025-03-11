package com.financialsystem.dto.database.project;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.dto.request.EmployeeRequestForSalaryProject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SalaryProjectRequestDatabaseDto {
    private Long id;
    private Long enterpriseId;
    private Long bankId;
    private Currency currency;
    private List<EmployeeRequestForSalaryProject> employees;
}
