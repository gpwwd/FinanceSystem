package com.financialsystem.controller;

import com.financialsystem.service.DepositService;
import com.financialsystem.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/issue")
    public ResponseEntity<Long> issueLoan(@RequestParam Long accountId,
                                          @RequestParam BigDecimal amount,
                                          @RequestParam BigDecimal interestRate,
                                          @RequestParam int termMonths) {
        Long depositId = loanService.issueLoan(accountId, amount, interestRate, termMonths);
        return ResponseEntity.ok(depositId);
    }
}
