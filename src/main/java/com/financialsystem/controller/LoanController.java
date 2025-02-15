package com.financialsystem.controller;

import com.financialsystem.domain.LoanTerm;
import com.financialsystem.service.DepositService;
import com.financialsystem.service.LoanService;
import com.financialsystem.util.ValidLoanTerm;
import jakarta.validation.Valid;
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

    @PostMapping("/issue/custom-interest")
    public ResponseEntity<Long> issueLoanWithCustomInterestRate(@RequestParam Long accountId,
                                                                @RequestParam BigDecimal amount,
                                                                @RequestParam int termMonths) {
        Long depositId = loanService.issueLoanWithCustomInterestRate(accountId, amount, termMonths);
        return ResponseEntity.ok(depositId);
    }

    @PostMapping("/issue/fixed-interest")
    public ResponseEntity<Long> issueLoanWithFixedInterestRate(@RequestParam Long accountId,
                                                                @RequestParam BigDecimal amount,
                                                                @RequestParam @Valid @ValidLoanTerm String loanTerm) {
        Long depositId = loanService.issueLoanWithFixedInterestRate(accountId, amount, loanTerm);
        return ResponseEntity.ok(depositId);
    }
}
