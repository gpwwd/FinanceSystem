package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.transaction.TransactionType;
import com.financialsystem.dto.database.TransactionDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<TransactionDatabaseDto> {
    @Override
    public TransactionDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TransactionDatabaseDto(
                rs.getLong("id"),
                rs.getObject("from_entity_id", Long.class),
                rs.getObject("to_entity_id", Long.class),
                TransactionType.valueOf(rs.getString("from_type")),
                TransactionType.valueOf(rs.getString("to_type")),
                rs.getBigDecimal("amount"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getObject("revert_transaction_id", Long.class)
        );
    }
}
