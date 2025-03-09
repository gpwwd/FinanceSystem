package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.UserDatabaseDto;

import java.time.LocalDateTime;

public class Specialist extends NonClientUser{

    private Long enterpriseId;

    private Specialist(String fullName, String passport, String identityNumber,
                    String phone, String email, LocalDateTime createdAt,
                       String password, Long enterpriseId) {
        super(fullName, passport, identityNumber, phone, email, createdAt, password);
        this.enterpriseId = enterpriseId;
    }

    @Override
    protected void assignRole() {
        this.role = Role.SPECIALIST;
    }

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    public static Specialist create (String fullName, String passport, String identityNumber,
                                  String phone, String email, LocalDateTime createdAt,
                                     String password, Long enterpriseId){
        Specialist specialist = new Specialist(fullName, passport, identityNumber, phone, email, createdAt, password, enterpriseId);
        specialist.assignRole();
        return specialist;
    }
}
