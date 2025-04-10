package com.financialsystem.controller;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.dto.response.AccountResponseDto;
import com.financialsystem.dto.response.SalaryAccountResponseDto;
import com.financialsystem.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@PreAuthorize("hasAuthority('CLIENT')")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Long> createAccount(
            @AuthenticationPrincipal BankingUserDetails userDetails,
            @RequestParam Long bankId,
            @RequestParam Currency currency
    ) {
        Long accountId = accountService.createPersonalAccount(userDetails.getId(), bankId, currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
    }

    @DeleteMapping("/{accountId}/close")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Void> closeAccount(@PathVariable Long accountId, @AuthenticationPrincipal BankingUserDetails userDetails) {
        accountService.closeAccount(userDetails.getId(), accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<AccountResponseDto>> getAllAccountsForClient(@AuthenticationPrincipal BankingUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(
                accountService.getPersonalAccountsForClient(userDetails.getId())
        );
    }

    @GetMapping("{accountId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<AccountResponseDto> getAccountById(@AuthenticationPrincipal BankingUserDetails userDetails,
                                                             @PathVariable Long accountId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                accountService.getAccountById(userDetails.getId(), accountId)
        );
    }

    @GetMapping("salary/{accountId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<SalaryAccountResponseDto> getSalaryAccountById(@AuthenticationPrincipal BankingUserDetails userDetails,
                                                                         @PathVariable Long accountId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                accountService.getSalaryAccountById(userDetails.getId(), accountId)
        );
    }

    @GetMapping("replenish/{accountId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<AccountResponseDto> replenishAccount(@AuthenticationPrincipal BankingUserDetails userDetails,
                                                               @PathVariable Long accountId, BigDecimal amount) {
        return ResponseEntity.status(HttpStatus.OK).body(
                accountService.replenish(userDetails.getId(), accountId, amount)
        );
    }
}
