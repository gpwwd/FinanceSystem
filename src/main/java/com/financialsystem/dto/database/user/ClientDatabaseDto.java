package com.financialsystem.dto.database.user;

import com.financialsystem.domain.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class ClientDatabaseDto extends UserDatabaseDto {
    protected boolean isForeign;

    public ClientDatabaseDto(Long id, String fullName, String passport, String identityNumber, String phone,
                             String email, Role role, String password, LocalDateTime createdAt, boolean isForeign) {
        super(id, fullName, passport, identityNumber, phone, email, role, password, createdAt);
        this.isForeign = isForeign;
    }
}

