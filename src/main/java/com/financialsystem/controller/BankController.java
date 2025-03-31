package com.financialsystem.controller;

import com.financialsystem.dto.response.BankResponseDto;
import com.financialsystem.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/banks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("{bankId}")
    public ResponseEntity<BankResponseDto> getBankById(@PathVariable Long bankId) {
        return ResponseEntity.ok(
                bankService.getBankById(bankId)
        );
    }

    @GetMapping
    public ResponseEntity<List<BankResponseDto>> getAllBanks() {
        return ResponseEntity.ok(
                bankService.getAllBanks()
        );
    }

}
