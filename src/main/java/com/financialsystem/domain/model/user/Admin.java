package com.financialsystem.domain.model.user;

import com.financialsystem.dto.user.UserDatabaseDto;

import java.time.LocalDateTime;

public class Admin extends NonClientUser {
    private Admin(String fullName, String passport, String email, String phone, String passwordHash, LocalDateTime createdAt) {
        super(fullName, passport, email, phone, passwordHash, createdAt);
    }

    @Override
    protected void assignRole() {
        this.role = Role.ADMIN;
    }

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    public static Admin create(String fullName, String passport, String email, String phone, String passwordHash, LocalDateTime createdAt) {
        // Здесь можно добавить валидацию или другие логические проверки
        // Например, проверка на уникальность email или корректность паспорта
        Admin admin = new Admin(fullName, passport, email, phone, passwordHash, createdAt);
        admin.assignRole();
        return admin;
    }
}
