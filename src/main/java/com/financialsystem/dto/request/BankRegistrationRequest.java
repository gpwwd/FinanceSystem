package com.financialsystem.dto.request;

public record BankRegistrationRequest (
        String name,
        String bik,
        String address
) {
}
