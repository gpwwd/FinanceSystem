package com.financialsystem.dto.database.user;

import com.financialsystem.domain.model.user.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=true)
public class SpecialistDatabaseDto extends UserDatabaseDto {
    protected Long enterpriseId;

    public SpecialistDatabaseDto(Long id, String fullName, String passport, String identityNumber, String phone,
                             String email, Role role, String password, LocalDateTime createdAt, Long enterpriseId) {
        super(id, fullName, passport, identityNumber, phone, email, role, password, createdAt);
        this.enterpriseId = enterpriseId;
    }
}
