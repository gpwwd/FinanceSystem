package com.financialsystem.mapper;

import com.financialsystem.domain.model.transaction.Transaction;
import com.financialsystem.dto.database.TransactionDatabaseDto;
import com.financialsystem.dto.response.TransactionResponseDto;

public class TransactionMapper {

    public static TransactionResponseDto toResponseDto(Transaction t) {
        return toResponseDto(t.toDto());
    }

    public static TransactionResponseDto toResponseDto(TransactionDatabaseDto d) {
        return new TransactionResponseDto(
                d.getId(), d.getFromEntityId(), d.getToEntityId(), d.getRevertTransactionId(), d.getFromType(), d.getToType(), d.getAmount(), d.getTimestamp()
        );
    }
}
