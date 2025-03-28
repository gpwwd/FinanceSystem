package com.financialsystem.dto.request;

public record ClientRegistrationRequest(
        String fullName,
        String passport,
        String identityNumber,
        String phone,
        String email,
        String password,
        boolean isForeign
) {}
