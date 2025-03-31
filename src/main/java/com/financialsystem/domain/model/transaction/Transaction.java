package com.financialsystem.domain.model.transaction;

import com.financialsystem.dto.database.TransactionDatabaseDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@AllArgsConstructor
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Transaction {
    private Long id;
    private Long fromEntityId;
    private Long toEntityId;
    private TransactionType fromType;
    private TransactionType toType;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    @Setter
    private Long revertTransactionId;

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

    public static Transaction fromDto(TransactionDatabaseDto transaction) {
        return new Transaction(transaction.getId(), transaction.getFromEntityId(), transaction.getToEntityId(), transaction.getFromType(),
                transaction.getToType(), transaction.getAmount(), transaction.getTimestamp(), transaction.getRevertTransactionId());
    }

    public TransactionDatabaseDto toDto() {
        return new TransactionDatabaseDto(id, fromEntityId, toEntityId, fromType, toType, amount, timestamp, revertTransactionId);
    }
}
