package com.financialsystem.domain.model;

import com.financialsystem.domain.status.SalaryProjectStatus;
import com.financialsystem.dto.database.SalaryProjectDatabaseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SalaryProject {
    @Getter
    private Long id;
    private Long enterpriseId;
    private Long bankId;
    @Getter
    private Currency currency;
    private LocalDateTime createdAt;
    private SalaryProjectStatus status;

    public static SalaryProject createSalaryProjectRequest(Long enterpriseId, Long bankId, Currency currency) {
        SalaryProject salaryProject = new SalaryProject();
        salaryProject.enterpriseId = enterpriseId;
        salaryProject.bankId = bankId;
        salaryProject.currency = currency;
        salaryProject.createdAt = LocalDateTime.now();
        salaryProject.status = SalaryProjectStatus.PENDING;
        return salaryProject;
    }

    public void approveProjectRequest() {
        this.status = SalaryProjectStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public SalaryProjectDatabaseDto toDto() {
        return new SalaryProjectDatabaseDto(id, enterpriseId, bankId, currency, createdAt, status);
    }

    public static SalaryProject fromDto(SalaryProjectDatabaseDto dto) {
        return new SalaryProject(dto.getId(), dto.getEnterpriseId(), dto.getBankId(), dto.getCurrency(),
                dto.getCreatedAt(), dto.getStatus());
    }
}
