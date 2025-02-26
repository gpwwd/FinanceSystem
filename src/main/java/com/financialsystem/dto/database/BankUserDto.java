package com.financialsystem.dto.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankUserDto {
    private final Long clientId;
    private final Long bankId;
}
