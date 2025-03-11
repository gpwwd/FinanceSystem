package com.financialsystem.controller;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.dto.request.EmployeeRequestForSalaryProject;
import com.financialsystem.dto.request.SalaryProjectRequest;
import com.financialsystem.dto.response.EmployeeResponseForSalaryProject;
import com.financialsystem.service.SalaryProjectService;
import com.financialsystem.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialist")
@PreAuthorize("hasAuthority('SPECIALIST')")
public class SpecialistController {

    private final SalaryProjectService salaryProjectService;

    @Autowired
    public SpecialistController(SalaryProjectService salaryProjectService) {
        this.salaryProjectService = salaryProjectService;
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
}
