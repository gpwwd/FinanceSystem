package com.financialsystem.domain.model;

import com.financialsystem.domain.status.SalaryProjectStatus;
import com.financialsystem.dto.database.project.SalaryProjectDatabaseDto;
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
    @Getter
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

    public void checkStatusOrThrow(SalaryProjectStatus status) {
        if (!this.status.equals(status)) {
            throw new IllegalStateException("Salary project status mismatch, " +
                    "should be " + status + " but is " + this.status);
        }
    }

    public boolean isStatus(SalaryProjectStatus status) {
        return this.status.equals(status);
    }

    public void checkStatusToAddEmployee(){
        if(!this.status.equals(SalaryProjectStatus.ACTIVE) && !this.status.equals(SalaryProjectStatus.PENDING)) {
            throw new IllegalStateException("Salary project status should be ACTIVE or PENDING");
        }
    }

    public SalaryProjectDatabaseDto toDto() {
        return new SalaryProjectDatabaseDto(id, enterpriseId, bankId, currency, createdAt, status);
    }

    public static SalaryProject fromDto(SalaryProjectDatabaseDto dto) {
        return new SalaryProject(dto.getId(), dto.getEnterpriseId(), dto.getBankId(), dto.getCurrency(),
                dto.getCreatedAt(), dto.getStatus());
    }
}
