package com.financialsystem.controller;

import com.financialsystem.domain.model.DepositTerm;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.dto.response.DepositTermDto;
import com.financialsystem.dto.response.LoanTermDto;
import com.financialsystem.dto.response.PendingLoanResponseDto;
import com.financialsystem.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.financialsystem.domain.strategy.FixedInterestStrategy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/loans/create-request")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Long> requestLoan(@RequestParam Long accountId,
                                            @RequestParam BigDecimal amount,
                                            @RequestParam int termMonths,
                                            @RequestParam boolean isFixedInterest,
                                            @AuthenticationPrincipal BankingUserDetails userDetails
    ) {
        Long requestLoanId = loanService.createLoanRequest(userDetails, accountId, amount, termMonths, isFixedInterest);
        return ResponseEntity.ok(requestLoanId);
    }

    @PostMapping("/approve/{pendingLoanId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Long> approveLoan(@PathVariable Long pendingLoanId) {
        Long loanId = loanService.approveLoan(pendingLoanId);
        return ResponseEntity.ok(loanId);
    }

    @PostMapping("/reject/{pendingLoanId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Long> rejectLoan(@PathVariable Long pendingLoanId) {
        Long loanId = loanService.rejectLoan(pendingLoanId);
        return ResponseEntity.ok(loanId);
    }

    @PostMapping("/{loanId}/pay")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Long> makeLoanPayment(@RequestParam BigDecimal amount,
                                                @PathVariable Long loanId,
                                                @AuthenticationPrincipal BankingUserDetails userDetails) {
        Long payedLoanId = loanService.makePayment(userDetails, amount, loanId);
        return ResponseEntity.ok(payedLoanId);
    }

    @GetMapping("/pending/{accountId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<PendingLoanResponseDto>> getPendingLoansForAccount(@PathVariable Long accountId,
                                                                                  @AuthenticationPrincipal BankingUserDetails userDetails) {
        List<PendingLoanResponseDto> pendingLoans = loanService.getPendingLoansForUserAccount(userDetails, accountId);
        return ResponseEntity.ok(pendingLoans);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<PendingLoanResponseDto>> getAllPendingLoans() {
        List<PendingLoanResponseDto> pendingLoans = loanService.getPendingLoans();
        return ResponseEntity.ok(pendingLoans);
    }

    @GetMapping("/fixed-terms")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public ResponseEntity<List<LoanTermDto>> getAvailableLoanTerms() {
        List<LoanTermDto> terms = FixedInterestStrategy.getAllFixedLoanTerms();
        return ResponseEntity.ok(terms);
    }
}
