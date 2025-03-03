package com.financialsystem.controller;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@PreAuthorize("hasAuthority('CLIENT')")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Long> createAccount(
            @AuthenticationPrincipal BankingUserDetails userDetails,
            @RequestParam Long bankId,
            @RequestParam Currency currency,
            @RequestParam boolean isAccountForSalary
    ) {
        Long accountId = accountService.createAccount(userDetails.getId(), bankId, currency, isAccountForSalary);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
    }

//    @DeleteMapping("/{accountId}/close")
//    public ResponseEntity<Void> closeAccount(@PathVariable Long accountId, @RequestParam Long clientId) {
//        Long deletedAccountId = accountService.closeAccount(clientId, accountId);
//        return ResponseEntity.noContent().build(); // 204 No Content
//    }
}
