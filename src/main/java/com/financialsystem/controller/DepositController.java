package com.financialsystem.controller;

import com.financialsystem.domain.model.Deposit;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.service.DepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/deposits")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Long> createDeposit(
            @AuthenticationPrincipal BankingUserDetails userDetails,
            @RequestParam Long accountId,
            @RequestParam BigDecimal interestRate,
            @RequestParam int termMonths,
            @RequestParam BigDecimal principalBalance) {
        Long depositId = depositService.create(userDetails.getId(), accountId, interestRate, termMonths, principalBalance);
        return ResponseEntity.ok(depositId);
    }

    @PostMapping("/{id}/withdraw-interest")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Deposit> withdrawInterest(@AuthenticationPrincipal BankingUserDetails userDetails,
                                                    @PathVariable Long id, @RequestParam BigDecimal amount) {
        Deposit deposit = depositService.withdrawInterest(userDetails.getId(), id, amount);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/{id}/replenish")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Deposit> replenish(@AuthenticationPrincipal BankingUserDetails userDetails,
                                             @PathVariable Long id, @RequestParam BigDecimal amount) {
        Deposit deposit = depositService.replenish(userDetails.getId(), id, amount);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/{id}/retrieve")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<BigDecimal> retrieve(@AuthenticationPrincipal BankingUserDetails userDetails,
                                               @PathVariable Long id) {
        BigDecimal retrievedMoney = depositService.retrieveMoney(userDetails.getId(), id);
        return ResponseEntity.ok(retrievedMoney);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Void> transfer(@AuthenticationPrincipal BankingUserDetails userDetails,
                                         @RequestParam Long fromId,
                                         @RequestParam Long toId,
                                         @RequestParam BigDecimal amount) {
        depositService.transfer(userDetails.getId(), fromId, toId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Long> blockDeposit(@PathVariable Long id) {
        depositService.blockDeposit(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<Long> unblockDeposit(@PathVariable Long id) {
         depositService.unblockDeposit(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/freeze")
    public ResponseEntity<Long> freezeDeposit(@PathVariable Long id) {
        depositService.freezeDeposit(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/unfreeze")
    public ResponseEntity<Long> unfreezeDeposit(@PathVariable Long id) {
        depositService.unfreezeDeposit(id);
        return ResponseEntity.ok(id);
    }
}
