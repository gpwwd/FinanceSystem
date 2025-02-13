package com.financialsystem.controller;

import com.financialsystem.domain.Deposit;
import com.financialsystem.service.DepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/deposit")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createDeposit(@RequestParam String accountNumber,
                                              @RequestParam double annualInterestRate) {
        Long depositId = depositService.create(accountNumber, annualInterestRate);
        return ResponseEntity.ok(depositId);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Deposit> withdraw(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Deposit deposit = depositService.withdraw(id, amount);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/{id}/replenish")
    public ResponseEntity<Deposit> replenish(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Deposit deposit = depositService.replenish(id, amount);
        return ResponseEntity.ok(deposit);
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
        Long updatedId = depositService.blockDeposit(id);
        return ResponseEntity.ok(updatedId);
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<Long> unblockDeposit(@PathVariable Long id) {
        Long updatedId = depositService.unblockDeposit(id);
        return ResponseEntity.ok(updatedId);
    }

    @PostMapping("/{id}/freeze")
    public ResponseEntity<Long> freezeDeposit(@PathVariable Long id) {
        Long updatedId = depositService.freeDeposit(id);
        return ResponseEntity.ok(updatedId);
    }

    @PostMapping("/{id}/unfreeze")
    public ResponseEntity<Long> unfreezeDeposit(@PathVariable Long id) {
        Long updatedId = depositService.unfreeDeposit(id);
        return ResponseEntity.ok(updatedId);
    }
}
