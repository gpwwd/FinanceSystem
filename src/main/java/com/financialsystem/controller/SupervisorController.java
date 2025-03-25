package com.financialsystem.controller;

import com.financialsystem.dto.response.*;
import com.financialsystem.service.EnterpriseService;
import com.financialsystem.service.LoanService;
import com.financialsystem.service.ManagerService;
import com.financialsystem.service.SalaryProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supervisor")
@PreAuthorize("hasAuthority('OPERATOR') or hasAuthority('MANAGER') or hasAuthority('ADMIN')")
public class SupervisorController {

    private final SalaryProjectService salaryProjectService;
    private final EnterpriseService enterpriseService;
    private final ManagerService managerService;
    private final LoanService loanService;

    @Autowired
    public SupervisorController(SalaryProjectService salaryProjectService, EnterpriseService enterpriseService,
                                ManagerService managerService, LoanService loanService) {
        this.salaryProjectService = salaryProjectService;
        this.enterpriseService = enterpriseService;
        this.managerService = managerService;
        this.loanService = loanService;
    }

    @PostMapping("/salary-projects/approve/{pendingProjectId}")
    public ResponseEntity<Long> approveSalaryProject(@PathVariable Long pendingProjectId) {
        Long approvedId = salaryProjectService.approveProject(pendingProjectId);
        return ResponseEntity.ok(approvedId);
    }

    //test
    @PostMapping("/salary-projects/execute-projects")
    public ResponseEntity<Void> executeSalaryProject() {
        salaryProjectService.executeProjectMonthlySalary();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/enterprise/{id}")
    public ResponseEntity<EnterpriseResponseDto> getEnterpriseById(@PathVariable Long id) {
        return ResponseEntity.ok(
                enterpriseService.getEnterpriseById(id)
        );
    }

    @GetMapping("/salary-projects")
    public ResponseEntity<List<SalaryProjectResponseDto>> getAllSalaryProjects() {
        return ResponseEntity.ok(
                salaryProjectService.getAllSalaryProjects()
        );
    }

    @GetMapping("/salary-project/{id}/details")
    public ResponseEntity<SalaryProjectDetailsResponseDto> getSalaryProjectDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(
                salaryProjectService.getSalaryProjectDetails(id)
        );
    }

    @GetMapping("/enterprises")
    public ResponseEntity<List<EnterpriseResponseDto>> getAllEnterprises() {
        return ResponseEntity.ok(
                enterpriseService.getAllEnterprises()
        );
    }

    @GetMapping("/pending-clients")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<ClientResponseDto>> getAllPendingClients() {
        List<ClientResponseDto> pendingClients = managerService.getAllPendingClients();
        return ResponseEntity.ok(pendingClients);
    }

    @GetMapping("/approved-clients")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<ClientResponseDto>> getAllApprovedClients() {
        List<ClientResponseDto> clients = managerService.getAllApprovedClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/pending-loans")
    public ResponseEntity<List<PendingLoanResponseDto>> getAllPendingLoans() {
        List<PendingLoanResponseDto> pendingLoans = loanService.getPendingLoans();
        return ResponseEntity.ok(pendingLoans);
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        List<LoanResponseDto> loans = loanService.getLoans();
        return ResponseEntity.ok(loans);
    }
}
