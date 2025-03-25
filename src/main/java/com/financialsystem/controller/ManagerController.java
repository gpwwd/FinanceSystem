package com.financialsystem.controller;

import com.financialsystem.dto.response.ClientResponseDto;
import com.financialsystem.service.LoanService;
import com.financialsystem.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('MANAGER')")
public class ManagerController {

    private final ManagerService managerService;
    private final LoanService loanService;

    @Autowired
    public ManagerController(ManagerService managerService, LoanService loanService) {
        this.managerService = managerService;
        this.loanService = loanService;
    }

    @PostMapping("clients/{pendingClientId}/approve")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Long> approveClient(@PathVariable Long pendingClientId
                                              ) {
        Long approvedId = managerService.approveClient(pendingClientId);
        return ResponseEntity.ok(approvedId);
    }

    @PostMapping("clients/{pendingClientId}/reject")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Long> rejectClient(@PathVariable Long pendingClientId
                                             ) {
        Long approvedId = managerService.rejectClient(pendingClientId);
        return ResponseEntity.ok(approvedId);
    }

    @PostMapping("/loans/{pendingLoanId}/approve")
    public ResponseEntity<Long> approveLoan(@PathVariable Long pendingLoanId) {
        Long loanId = loanService.approveLoan(pendingLoanId);
        return ResponseEntity.ok(loanId);
    }

    @PostMapping("/loans/{pendingLoanId}/reject")
    public ResponseEntity<Long> rejectLoan(@PathVariable Long pendingLoanId) {
        Long loanId = loanService.rejectLoan(pendingLoanId);
        return ResponseEntity.ok(loanId);
    }

}
