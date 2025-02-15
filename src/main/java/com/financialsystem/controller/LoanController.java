package com.financialsystem.controller;

import com.financialsystem.service.LoanService;
import com.financialsystem.util.ValidLoanTerm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{loanId}/pay")
    public ResponseEntity<Long> makeLoanPayment(@RequestParam BigDecimal amount,
                                                @PathVariable Long loanId) {
        Long depositId = loanService.makePayment(amount, loanId);
        return ResponseEntity.ok(depositId);
    }
}
