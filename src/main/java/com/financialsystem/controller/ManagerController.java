package com.financialsystem.controller;

import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import com.financialsystem.dto.response.PendingClientResponseDto;
import com.financialsystem.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('MANAGER')")
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping("/approve/{pendingClientId}")
    public ResponseEntity<Long> approveClient(@PathVariable Long pendingClientId
                                              //@AuthenticationPrincipal CustomUserDetails userDetails,
                                              ) {
        //Long managerId = userDetails.getId();
        Long approvedId = managerService.approveClient(pendingClientId);
        return ResponseEntity.ok(approvedId);
    }

    @PostMapping("/reject/{pendingClientId}")
    public ResponseEntity<Long> rejectClient(@PathVariable Long pendingClientId
                                             //@AuthenticationPrincipal CustomUserDetails userDetails,
                                             ) {
        //Long managerId = userDetails.getId();
        Long approvedId = managerService.rejectClient(pendingClientId);
        return ResponseEntity.ok(approvedId);
    }

    @GetMapping("/pending-clients")
    public ResponseEntity<List<PendingClientResponseDto>> getAllPendingClients() {
        List<PendingClientResponseDto> pendingClients = managerService.getAllPendingClients();
        return ResponseEntity.ok(pendingClients);
    }
}
