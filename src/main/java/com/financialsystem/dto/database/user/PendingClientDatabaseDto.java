package com.financialsystem.dto.database.user;

import com.financialsystem.domain.model.user.PendingClientStatus;
import com.financialsystem.domain.model.user.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class PendingClientDatabaseDto extends ClientDatabaseDto {
    @Getter @Setter
    private PendingClientStatus status;

    public PendingClientDatabaseDto(Long id, String fullName, String passport,
                             String identityNumber, String phone,
                             String email, Role role, String password,
                             LocalDateTime createdAt, boolean isForeign, PendingClientStatus status) {
        super(id, fullName, passport, identityNumber, phone, email, role, password, createdAt, isForeign);
        this.status = status;
    }
}
