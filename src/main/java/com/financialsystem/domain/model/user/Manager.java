package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.dto.database.user.UserDatabaseDto;

import java.time.LocalDateTime;
import java.util.List;

public class Manager extends NonClientUser{
    private Manager(String fullName, String passport, String email, String phone, String passwordHash, LocalDateTime createdAt, List<Long> banksIds) {
        super(fullName, passport, email, phone, passwordHash, createdAt, banksIds);
    }

    @Override
    protected void assignRole() {
        this.role = Role.MANAGER;
    }

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    public static Manager create (String fullName, String passport, String email, String phone,
                                  String passwordHash, LocalDateTime createdAt, List<Long> banksIds){
        Manager manager = new Manager(fullName, passport, email, phone, passwordHash, createdAt, banksIds);
        manager.assignRole();
        return manager;
    }
}
