package com.financialsystem.dto.database;

import com.financialsystem.domain.model.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionDatabaseDto {
    private Long id;
    private Long fromEntityId;
    private Long toEntityId;
    private TransactionType fromType;
    private TransactionType toType;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
