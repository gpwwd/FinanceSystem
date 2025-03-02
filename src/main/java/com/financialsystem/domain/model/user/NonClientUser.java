package com.financialsystem.domain.model.user;

import java.time.LocalDateTime;
import java.util.List;

public abstract class NonClientUser extends User {
    protected NonClientUser(String fullName, String passport, String identityNumber,
                            String phone, String email, LocalDateTime createdAt, String password) {
        super(fullName, passport, identityNumber, phone, email, createdAt, password);
    }
}
