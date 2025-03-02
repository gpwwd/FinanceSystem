package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.UserDatabaseDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

public abstract class User  {
    protected Long id;
    protected String fullName;
    protected String passport; // сделать отдельеный value object
    protected String identityNumber;
    protected String phone;
    protected String email;
    protected Role role;
    protected String password;
    protected LocalDateTime createdAt;

    protected User(UserDatabaseDto user) {
        id = user.getId();
        fullName = user.getFullName();
        passport = user.getPassport();
        identityNumber = user.getIdentityNumber();
        phone = user.getPhone();
        email = user.getEmail();
        role = user.getRole();
        createdAt = user.getCreatedAt();
        password = user.getPassword();
    }

    protected User(String fullName, String passport, String identityNumber,
                String phone, String email, LocalDateTime createdAt, String password) {
        this.fullName = fullName;
        this.passport = passport;
        this.identityNumber = identityNumber;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.password = password;
    }

    protected abstract void assignRole();
    public abstract UserDatabaseDto toDto();
}
