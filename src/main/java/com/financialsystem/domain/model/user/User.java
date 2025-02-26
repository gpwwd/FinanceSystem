package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.UserDatabaseDto;

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
    protected LocalDateTime createdAt;
    protected List<Long> banksIds = new ArrayList<>();

    protected User(UserDatabaseDto user) {
        id = user.getId();

        Objects.requireNonNull(user.getBanksIds(), "banksIds не может быть пустым");
        if (user.getBanksIds().isEmpty()) {
            throw new IllegalArgumentException("Пользователь должен быть связан хотя бы с одним банком");
        }

        fullName = user.getFullName();
        passport = user.getPassport();
        identityNumber = user.getIdentityNumber();
        phone = user.getPhone();
        email = user.getEmail();
        role = user.getRole();
        createdAt = user.getCreatedAt();
        banksIds = user.getBanksIds();
    }

    protected User(String fullName, String passport, String identityNumber,
                String phone, String email, LocalDateTime createdAt, List<Long> banksIds) {
        Objects.requireNonNull(banksIds, "banksIds не может быть пустым");
        if (banksIds.isEmpty()) {
            throw new IllegalArgumentException("Пользователь должен быть связан хотя бы с одним банком");
        }

        this.fullName = fullName;
        this.passport = passport;
        this.identityNumber = identityNumber;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.banksIds = banksIds;
    }

    protected abstract void assignRole();
    public abstract UserDatabaseDto toDto();
}
