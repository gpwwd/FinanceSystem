package com.financialsystem.domain.model;

import com.financialsystem.dto.database.TransactionDatabaseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@AllArgsConstructor
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction {
    private Long id;
    private Long fromEntityId;
    private Long toEntityId;
    private TransactionType fromType;
    private TransactionType toType;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    private Transaction(Long fromEntityId, TransactionType fromType, Long toEntityId, TransactionType toType, BigDecimal amount) {
        this.fromEntityId = fromEntityId;
        this.toEntityId = toEntityId;
        this.fromType = fromType;
        this.toType = toType;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public static Transaction create(Long fromEntityId, TransactionType fromType, Long toEntityId, TransactionType toType, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }
        return new Transaction(fromEntityId, fromType, toEntityId, toType, amount);
    }

    public TransactionDatabaseDto toDto() {
        return new TransactionDatabaseDto(id, fromEntityId, toEntityId, fromType, toType, amount, timestamp);
    }
}
