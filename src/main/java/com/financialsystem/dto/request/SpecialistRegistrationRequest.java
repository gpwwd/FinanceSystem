package com.financialsystem.dto.request;

public record SpecialistRegistrationRequest(
        String fullName,
        String passport,
        String identityNumber,
        String phone,
        String email,
        String password,
        Long enterpriseId
) {
}
