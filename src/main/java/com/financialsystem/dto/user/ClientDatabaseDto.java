package com.financialsystem.dto.user;

import com.financialsystem.domain.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDatabaseDto extends UserDatabaseDto {
    protected boolean isForeign;
    public ClientDatabaseDto(Long id, String fullName, String passport,
                             String identityNumber, String phone,
                             String email, Role role,
                             LocalDateTime createdAt, boolean isForeign, Long enterpriseId) {
        super(id, fullName, passport, identityNumber, phone, email, role, createdAt, enterpriseId);
        this.isForeign = isForeign;
    }
}
