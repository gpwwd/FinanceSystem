package com.financialsystem.dto.response;

import com.financialsystem.domain.model.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private Long fromEntityId;
    private Long toEntityId;
    private Long revertTransactionId;
    private TransactionType fromType;
    private TransactionType toType;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
