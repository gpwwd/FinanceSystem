package com.financialsystem.service;

import com.financialsystem.domain.model.Bank;
import com.financialsystem.domain.model.account.Account;
import com.financialsystem.dto.response.BankResponseDto;
import com.financialsystem.repository.BankRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    private final BankRepository bankRepository;
    private final EntityFinder entityFinder;

    public BankService(BankRepository bankRepository, EntityFinder entityFinder) {
        this.bankRepository = bankRepository;
        this.entityFinder = entityFinder;
    }

    public BankResponseDto getBankById(Long bankId) {
        Bank bank = entityFinder.findEntityById(bankId, bankRepository, "Банк");
        return bank.toBankResponseDto();
    }

    public List<BankResponseDto> getAllBanks() {
        List<Bank> banks = bankRepository.findAll();
        return banks.stream().map(Bank::toBankResponseDto).toList();
    }
}
