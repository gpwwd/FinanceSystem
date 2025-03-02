package com.financialsystem.dto.database.user;

import com.financialsystem.domain.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserDatabaseDto {
    protected Long id;
    protected String fullName;
    protected String passport;
    protected String identityNumber;
    protected String phone;
    protected String email;
    protected Role role;
    protected String password;
    protected LocalDateTime createdAt;
}
