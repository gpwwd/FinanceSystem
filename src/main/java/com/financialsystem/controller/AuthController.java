package com.financialsystem.controller;

import com.financialsystem.domain.model.Enterprise;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import com.financialsystem.dto.request.EnterpriseRegistrationRequest;
import com.financialsystem.dto.request.LoginRequestDto;
import com.financialsystem.dto.request.SpecialistRegistrationRequest;
import com.financialsystem.dto.response.UserAuthResponseDto;
import com.financialsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/clients/register")
    public ResponseEntity<Long> registerClient(@RequestBody ClientRegistrationRequest request) {
        Long clientId = authService.registerClient(request);
        return ResponseEntity.ok(clientId);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponseDto> login(
            @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(
                authService.login(request.getUsername(), request.getPassword())
        );
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping("/specialists/register")
    public ResponseEntity<Long> registerSpecialist(@RequestBody SpecialistRegistrationRequest request) {
        Long clientId = authService.registerSpecialist(request);
        return ResponseEntity.ok(clientId);
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping("/enterprises/register")
    public ResponseEntity<Long> registerEnterprise(@RequestBody EnterpriseRegistrationRequest request) {
        Long clientId = authService.registerEnterprise(request);
        return ResponseEntity.ok(clientId);
    }
}
