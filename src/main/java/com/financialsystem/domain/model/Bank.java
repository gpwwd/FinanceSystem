package com.financialsystem.domain.model;


import com.financialsystem.dto.database.BankDatabaseDto;
import com.financialsystem.dto.request.BankRegistrationRequest;
import com.financialsystem.dto.response.BankResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Bank {
    @Getter
    private Long id;
    private String name;
    private String bik;
    private String address;

    public static Bank create(String name, String bik, String address) {
        Bank bank = new Bank();
        bank.name = name;
        bank.bik = bik;
        bank.address = address;
        return bank;
    }

    public static Bank create(BankRegistrationRequest request) {
        return create(request.name(), request.bik(), request.address());
    }

    public BankDatabaseDto toDto() {
        return new BankDatabaseDto(id, name, bik, address);
    }

    public static Bank fromDto(BankDatabaseDto dto) {
        return new Bank(dto.getId(), dto.getName(), dto.getBik(), dto.getAddress());
    }

    public BankResponseDto toBankResponseDto() {
        return new BankResponseDto(id, name, bik, address);
    }
}