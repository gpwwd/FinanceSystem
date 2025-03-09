package com.financialsystem.dto.request;

public record EnterpriseRegistrationRequest(
        String type,
        String legalName,
        String unp,
        Long bankId,
        String legalAddress
) {}
