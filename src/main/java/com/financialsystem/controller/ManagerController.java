package com.financialsystem.controller;

import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import com.financialsystem.dto.response.PendingClientResponseDto;
import com.financialsystem.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping("/approve/{pendingClientId}")
    public ResponseEntity<Long> approveClient(@PathVariable Long pendingClientId) {
        Long approvedId = managerService.approveClient(pendingClientId);
        return ResponseEntity.ok(approvedId);
    }

    @GetMapping("/pending-clients")
    public ResponseEntity<List<PendingClientResponseDto>> getAllPendingClients() {
        List<PendingClientResponseDto> pendingClients = managerService.getAllPendingClients();
        return ResponseEntity.ok(pendingClients);
    }
}
