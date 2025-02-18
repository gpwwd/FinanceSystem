package com.financialsystem.mapper;

import com.financialsystem.domain.model.Loan;
import com.financialsystem.domain.status.LoanStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanRowMapper implements RowMapper<Loan> {
    @Override
    public Loan mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Loan(
                rs.getLong("id"),
                rs.getLong("account_id"),
                rs.getBigDecimal("principal_amount"),
                rs.getBigDecimal("remaining_balance"),
                rs.getBigDecimal("interest_rate"),
                rs.getInt("term_months"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                LoanStatus.valueOf(rs.getString("status"))
        );
    }
}
