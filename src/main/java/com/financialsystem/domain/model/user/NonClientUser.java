package com.financialsystem.domain.model.user;

import java.time.LocalDateTime;
import java.util.List;

public abstract class NonClientUser extends User {
    protected NonClientUser(String fullName, String passport, String email, String phone, String passwordHash, LocalDateTime createdAt, List<Long> banksIds) {
        super(fullName, passport, email, phone, passwordHash, createdAt, banksIds);
    }
}
