package com.financialsystem.controller;

import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.dto.request.EmployeeRequestForSalaryProject;
import com.financialsystem.dto.request.SalaryProjectRequest;
import com.financialsystem.dto.response.EmployeeResponseForSalaryProject;
import com.financialsystem.dto.response.EnterpriseResponseDto;
import com.financialsystem.dto.response.SalaryProjectDetailsResponseDto;
import com.financialsystem.dto.response.SalaryProjectResponseDto;
import com.financialsystem.service.EnterpriseService;
import com.financialsystem.service.SalaryProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/specialist")
@PreAuthorize("hasAuthority('SPECIALIST')")
public class SpecialistController {

    private final SalaryProjectService salaryProjectService;
    private final EnterpriseService enterpriseService;

    @Autowired
    public SpecialistController(SalaryProjectService salaryProjectService, EnterpriseService enterpriseService) {
        this.salaryProjectService = salaryProjectService;
        this.enterpriseService = enterpriseService;
    }

    @PostMapping("/salary-projects/execute-projects")
    public ResponseEntity<Void> executeSalaryProject() {
        salaryProjectService.executeProjectMonthlySalary();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/salary-projects/create-request")
    public ResponseEntity<Long> requestSalaryProject(@RequestBody SalaryProjectRequest request,
                                                     @AuthenticationPrincipal BankingUserDetails userDetails) {
        Long salaryProjectRequestId = salaryProjectService.createRequest(userDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryProjectRequestId);
    }

    @PostMapping("/salary-projects/add-employee")
    public ResponseEntity<EmployeeResponseForSalaryProject> addEmployeeToProject(@RequestBody EmployeeRequestForSalaryProject request,
                                                                                 @AuthenticationPrincipal BankingUserDetails userDetails) {
        EmployeeResponseForSalaryProject employeeResponseForSalaryProject = salaryProjectService.addEmployeeToSalaryProject(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeResponseForSalaryProject);
    }

    @GetMapping("/enterprise")
    public ResponseEntity<EnterpriseResponseDto> getEnterpriseById(@AuthenticationPrincipal BankingUserDetails userDetails) {
        return ResponseEntity.ok(
                enterpriseService.getEnterpriseById(userDetails)
        );
    }

    @GetMapping("/salary-project/{id}/details")
    @PreAuthorize("""
            @salaryProjectService.validateOwner(#id, authentication.principal)""")
    public ResponseEntity<SalaryProjectDetailsResponseDto> getSalaryProjectDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(
                salaryProjectService.getSalaryProjectDetails(id)
        );
    }
}
