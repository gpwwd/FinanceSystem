package com.financialsystem.controller;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import com.financialsystem.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Long> createAccount(
            @RequestParam Long clientId, // доставать из userDetails
            @RequestParam Long bankId,
            @RequestParam Currency currency,
            @RequestParam boolean isAccountForSalary
    ) {
        Long accountId = accountService.createAccount(clientId, bankId, currency, isAccountForSalary);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
    }

//    @DeleteMapping("/{accountId}/close")
//    public ResponseEntity<Void> closeAccount(@PathVariable Long accountId, @RequestParam Long clientId) {
//        Long deletedAccountId = accountService.closeAccount(clientId, accountId);
//        return ResponseEntity.noContent().build(); // 204 No Content
//    }
}
