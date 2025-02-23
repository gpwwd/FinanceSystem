package com.financialsystem.dto.user;

import com.financialsystem.domain.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDatabaseDto {
    protected Long id;
    protected String fullName;
    protected String passport;
    protected String identityNumber;
    protected String phone;
    protected String email;
    protected Role role;
    protected LocalDateTime createdAt;
    protected Long enterpriseId;
}
