package com.financialsystem.domain.model;

import com.financialsystem.dto.database.EnterpriseDatabaseDto;
import com.financialsystem.dto.request.EnterpriseRegistrationRequest;
import com.financialsystem.dto.response.EnterpriseResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Enterprise {
    @Getter
    private Long id;
    private String type;
    private String legalName;
    private String unp;
    @Getter
    private Long bankId;
    @Getter
    private Long payrollAccountId;
    private String legalAddress;
    private LocalDateTime createdAt;

    public static Enterprise create(String type, String legalName, String unp,
                                    Long bankId, String legalAddress, Long payrollAccountId) {
        Enterprise enterprise = new Enterprise();
        enterprise.type = type;
        enterprise.legalName = legalName;
        enterprise.unp = unp;
        enterprise.bankId = bankId;
        enterprise.payrollAccountId = payrollAccountId;
        enterprise.legalAddress = legalAddress;
        enterprise.createdAt = LocalDateTime.now();
        return enterprise;
    }

    public static Enterprise create(EnterpriseRegistrationRequest request, Long payrollAccountId) {
        return create(request.type(), request.legalName(), request.unp(), request.bankId(), request.legalAddress(), payrollAccountId);
    }

    public EnterpriseDatabaseDto toDto() {
        return new EnterpriseDatabaseDto(id, type, legalName, unp, bankId, legalAddress, createdAt, payrollAccountId);
    }

    public static Enterprise fromDto(EnterpriseDatabaseDto dto) {
        return new Enterprise(dto.id(), dto.type(), dto.legalName(), dto.unp(), dto.bankId(), dto.payrollAccountId(), dto.legalAddress(), dto.createdAt());
    }

    public EnterpriseResponseDto toResponseDto() {
        return new EnterpriseResponseDto(id, type, legalName, unp, bankId, payrollAccountId, legalAddress, createdAt);
    }

    public void connectPayrollAccount(Long payrollAccountId) {
        this.payrollAccountId = payrollAccountId;
    }
}
