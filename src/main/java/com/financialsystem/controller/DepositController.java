package com.financialsystem.controller;

import com.financialsystem.domain.model.Deposit;
import com.financialsystem.domain.model.DepositTerm;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.dto.response.DepositResponseDto;
import com.financialsystem.dto.response.DepositTermDto;
import com.financialsystem.service.DepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
            @RequestParam int termMonths,
            @RequestParam BigDecimal principalBalance) {
        Long depositId = depositService.create(userDetails.getId(), accountId,  termMonths, principalBalance);
        return ResponseEntity.ok(depositId);
    }

    @PostMapping("/{id}/withdraw-interest")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<DepositResponseDto> withdrawInterest(@AuthenticationPrincipal BankingUserDetails userDetails,
                                                    @PathVariable Long id, @RequestParam BigDecimal amount) {
        DepositResponseDto deposit = depositService.withdrawInterest(userDetails.getId(), id, amount);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/{id}/replenish")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<DepositResponseDto> replenish(@AuthenticationPrincipal BankingUserDetails userDetails,
                                             @PathVariable Long id, @RequestParam BigDecimal amount) {
        DepositResponseDto deposit = depositService.replenish(userDetails.getId(), id, amount);
        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/{id}/retrieve")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<BigDecimal> retrieve(@AuthenticationPrincipal BankingUserDetails userDetails,
                                               @PathVariable Long id) {
        BigDecimal retrievedMoney = depositService.retrieveMoney(userDetails.getId(), id);
        return ResponseEntity.ok(retrievedMoney);
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Long> blockDeposit(@PathVariable Long id) {
        depositService.blockDeposit(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Long> unblockDeposit(@PathVariable Long id) {
         depositService.unblockDeposit(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/freeze")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public ResponseEntity<Long> freezeDeposit(@AuthenticationPrincipal BankingUserDetails userDetails,
                                              @PathVariable Long id) {
        depositService.freezeDeposit(userDetails, id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/unfreeze")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public ResponseEntity<Long> unfreezeDeposit(@AuthenticationPrincipal BankingUserDetails userDetails,
                                                @PathVariable Long id) {
        depositService.unfreezeDeposit(userDetails, id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public ResponseEntity<List<DepositResponseDto>> getDepositsForAccount(@AuthenticationPrincipal BankingUserDetails userDetails,
                                                                       @PathVariable Long accountId) {
        List<DepositResponseDto> deposits = depositService.getDepositsForAccount(userDetails, accountId);
        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/terms")
    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public ResponseEntity<List<DepositTermDto>> getAvailableDepositTerms() {
        List<DepositTermDto> terms = Arrays.stream(DepositTerm.values())
                .map(term -> new DepositTermDto(term.getMonths(), term.getInterestRate()))
                .toList();

        return ResponseEntity.ok(terms);
    }
}
