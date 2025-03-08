package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.TransactionType;
import com.financialsystem.dto.database.TransactionDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<TransactionDatabaseDto> {
    @Override
    public TransactionDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TransactionDatabaseDto(
                rs.getLong("id"),
                rs.getLong("from_entity_id"),
                rs.getLong("to_entity_id"),
                TransactionType.valueOf(rs.getString("from_type")),
                TransactionType.valueOf(rs.getString("to_type")),
                rs.getBigDecimal("amount"),
                rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }
}
