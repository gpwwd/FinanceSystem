package com.financialsystem.dto.database.user;

import com.financialsystem.domain.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDatabaseDto extends UserDatabaseDto {
    protected boolean isForeign;
    public ClientDatabaseDto(Long id, String fullName, String passport,
                             String identityNumber, String phone,
                             String email, Role role,
                             LocalDateTime createdAt, boolean isForeign) {
        super(id, fullName, passport, identityNumber, phone, email, role, createdAt);
        this.isForeign = isForeign;
    }
}
