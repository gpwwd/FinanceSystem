package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.UserDatabaseDto;

import java.time.LocalDateTime;
import java.util.List;

public class Admin extends NonClientUser {
    private Admin(String fullName, String passport, String identityNumber,
                  String phone, String email, LocalDateTime createdAt, String password) {
        super(fullName, passport, identityNumber, phone, email, createdAt, password);
    }

    @Override
    protected void assignRole() {
        this.role = Role.ADMIN;
    }

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    public static Admin create(String fullName, String passport, String identityNumber,
                               String phone, String email, LocalDateTime createdAt, String password) {
        // Здесь можно добавить валидацию или другие логические проверки
        // Например, проверка на уникальность email или корректность паспорта
        Admin admin = new Admin(fullName, passport, identityNumber, phone, email, createdAt, encodePassword(password));
        admin.assignRole();
        return admin;
    }
}
