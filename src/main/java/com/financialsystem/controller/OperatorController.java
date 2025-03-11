package com.financialsystem.controller;

import com.financialsystem.service.SalaryProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operator")
@PreAuthorize("hasAuthority('OPERATOR')")
public class OperatorController {

    private final SalaryProjectService salaryProjectService;

    @Autowired
    public OperatorController(SalaryProjectService salaryProjectService) {
        this.salaryProjectService = salaryProjectService;
    }

    @PostMapping("/approve/{pendingProjectId}")
    public ResponseEntity<Long> approveSalaryProject(@PathVariable Long pendingProjectId) {
        Long approvedId = salaryProjectService.approveProject(pendingProjectId);
        return ResponseEntity.ok(approvedId);
    }
}
