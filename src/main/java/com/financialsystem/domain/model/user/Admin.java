package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.UserDatabaseDto;

import java.time.LocalDateTime;
import java.util.List;

public class Admin extends NonClientUser {
    private Admin(String fullName, String passport, String email, String phone, String passwordHash, LocalDateTime createdAt, List<Long> banksIds) {
        super(fullName, passport, email, phone, passwordHash, createdAt, banksIds);
    }

    @Override
    protected void assignRole() {
        this.role = Role.ADMIN;
    }

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    public static Admin create(String fullName, String passport, String email, String phone, String passwordHash, LocalDateTime createdAt, List<Long> banksIds) {
        // Здесь можно добавить валидацию или другие логические проверки
        // Например, проверка на уникальность email или корректность паспорта
        Admin admin = new Admin(fullName, passport, email, phone, passwordHash, createdAt, banksIds);
        admin.assignRole();
        return admin;
    }
}
