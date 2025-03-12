package com.financialsystem.dto.database;

import java.time.LocalDateTime;

public record EnterpriseDatabaseDto(
        Long id,
        String type,
        String legalName,
        String unp,
        Long bankId,
        String legalAddress,
        LocalDateTime createdAt,
        Long payrollAccountId
) {}