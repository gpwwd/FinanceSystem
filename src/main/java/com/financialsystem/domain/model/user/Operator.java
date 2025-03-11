package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.UserDatabaseDto;

import java.time.LocalDateTime;

public class Operator extends NonClientUser{
    private Operator(String fullName, String passport, String identityNumber,
            String phone, String email, LocalDateTime createdAt, String password) {
        super(fullName, passport, identityNumber, phone, email, createdAt, password);
    }

    @Override
    protected void assignRole() {
        this.role = Role.OPERATOR;
    }

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    public static Operator create (String fullName, String passport, String identityNumber,
            String phone, String email, LocalDateTime createdAt, String password){
        Operator operator = new Operator(fullName, passport, identityNumber, phone, email, createdAt, encodePassword(password));
        operator.assignRole();
        return operator;
    }
}