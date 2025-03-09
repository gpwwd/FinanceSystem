package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.dto.database.user.UserDatabaseDto;

import java.time.LocalDateTime;
import java.util.List;

public class Manager extends NonClientUser{
    private Manager(String fullName, String passport, String identityNumber,
                    String phone, String email, LocalDateTime createdAt, String password) {
        super(fullName, passport, identityNumber, phone, email, createdAt, password);
    }

    @Override
    protected void assignRole() {
        this.role = Role.MANAGER;
    }

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    public static Manager create (String fullName, String passport, String identityNumber,
                                  String phone, String email, LocalDateTime createdAt, String password){
        Manager manager = new Manager(fullName, passport, identityNumber, phone, email, createdAt, encodePassword(password));
        manager.assignRole();
        return manager;
    }
}
