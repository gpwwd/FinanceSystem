package com.financialsystem.dto.response;

import com.financialsystem.domain.status.SalaryProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EmployeeResponseForSalaryProject {
    private Long salaryProjectId;
    private Long enterpriseId;
    private Long bankId;
    private Long salaryAccountId;
    private String fullName;
    private String passport;
}
