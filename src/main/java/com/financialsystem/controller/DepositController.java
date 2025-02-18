package com.financialsystem.controller;

import com.financialsystem.domain.model.Deposit;
import com.financialsystem.service.DepositService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Long> createDeposit(@RequestParam Long accountId,
                                              @RequestParam BigDecimal interestRate,
                                              @RequestParam int termMonths,
                                              @RequestParam BigDecimal principalBalance) {
        Long depositId = depositService.create(accountId, interestRate, termMonths, principalBalance);
        return ResponseEntity.ok(depositId);
    }

    @PostMapping("/{id}/withdraw-interest")
    public ResponseEntity<Deposit> withdrawInterest(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Deposit deposit = depositService.withdrawInterest(id, amount);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/{id}/replenish")
    public ResponseEntity<Deposit> replenish(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Deposit deposit = depositService.replenish(id, amount);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/{id}/retrieve")
    public ResponseEntity<BigDecimal> retrieve(@PathVariable Long id) {
        BigDecimal retrievedMoney = depositService.retrieveMoney(id);
        return ResponseEntity.ok(retrievedMoney);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestParam Long fromId,
                                         @RequestParam Long toId,
                                         @RequestParam BigDecimal amount) {
        depositService.transfer(fromId, toId, amount);
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
