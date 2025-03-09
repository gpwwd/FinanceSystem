package com.financialsystem.domain.model;

import com.financialsystem.dto.database.EnterpriseDatabaseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Enterprise {
    @Getter
    private Long id;
    private String type;
    private String legalName;
    private String unp;
    private Long bankId;
    private String legalAddress;
    private LocalDateTime createdAt;

    public static Enterprise create(String type, String legalName, String unp,
                                    Long bankId, String legalAddress) {
        Enterprise enterprise = new Enterprise();
        enterprise.type = type;
        enterprise.legalName = legalName;
        enterprise.unp = unp;
        enterprise.bankId = bankId;
        enterprise.legalAddress = legalAddress;
        enterprise.createdAt = LocalDateTime.now();
        return enterprise;
    }

    public EnterpriseDatabaseDto toDto() {
        return new EnterpriseDatabaseDto(id, type, legalName, unp, bankId, legalAddress, createdAt);
    }

    public static Enterprise fromDto(EnterpriseDatabaseDto dto) {
        return new Enterprise(dto.id(), dto.type(), dto.legalName(), dto.unp(), dto.bankId(), dto.legalAddress(), dto.createdAt());
    }
}
