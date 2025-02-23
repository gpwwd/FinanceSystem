package com.financialsystem.domain.model.user;

import java.time.LocalDateTime;

public abstract class NonClientUser extends User {
    public NonClientUser(String fullName, String passport, String email, String phone, String passwordHash, LocalDateTime createdAt) {
        super(fullName, passport, email, phone, passwordHash, createdAt);
    }
}
