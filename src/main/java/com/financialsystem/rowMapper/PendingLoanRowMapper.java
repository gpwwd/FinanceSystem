package com.financialsystem.rowMapper;

import com.financialsystem.domain.status.PendingEntityStatus;
import com.financialsystem.dto.database.loan.PendingLoanDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PendingLoanRowMapper implements RowMapper<PendingLoanDatabaseDto> {
    @Override
    public PendingLoanDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PendingLoanDatabaseDto(
                rs.getLong("id"),
                rs.getLong("account_id"),
                rs.getBigDecimal("principal_amount"),
                rs.getInt("term_months"),
                PendingEntityStatus.valueOf(rs.getString("request_status")),
                rs.getBoolean("is_fixed_interest")
        );
    }
}
