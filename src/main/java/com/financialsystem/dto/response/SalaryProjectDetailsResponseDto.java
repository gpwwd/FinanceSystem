package com.financialsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SalaryProjectDetailsResponseDto {
    private List<SalaryAccountResponseDto> salaryAccounts;
    private AccountResponseDto enterpriseAccount;
    private SalaryProjectResponseDto salaryProject;
}
