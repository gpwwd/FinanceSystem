package com.financialsystem.controller;

import com.financialsystem.dto.response.TransactionResponseDto;
import com.financialsystem.mapper.TransactionMapper;
import com.financialsystem.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@PreAuthorize("hasAnyAuthority('MANAGER', 'ADMINISTRATOR', 'OPERATOR')")
public class TransactionController {

    private final TransactionsService service;

    @Autowired
    public TransactionController(TransactionsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAll() {
        return ResponseEntity.ok(
                service.getAll().stream()
                        .map(TransactionMapper::toResponseDto)
                        .toList()
        );
    }

    @PostMapping("/{id}/revert")
    public ResponseEntity<Long> revertTransaction(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                service.revertTransaction(id)
        );
    }
}
