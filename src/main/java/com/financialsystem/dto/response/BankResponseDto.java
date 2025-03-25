package com.financialsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankResponseDto {
    private Long id;
    private String name;
    private String bik;
    private String address;
}
