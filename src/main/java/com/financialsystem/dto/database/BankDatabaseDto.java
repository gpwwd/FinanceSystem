package com.financialsystem.dto.database;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankDatabaseDto {
    private Long id;
    private String name;
    private String bik;
    private String address;
}
