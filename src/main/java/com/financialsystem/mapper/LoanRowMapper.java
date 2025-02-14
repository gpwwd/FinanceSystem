package com.financialsystem.mapper;

import com.financialsystem.domain.Loan;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanRowMapper implements RowMapper<Loan> {
    @Override
    public Loan mapRow(ResultSet rs, int rowNum) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getLong("id"));
        loan.setAccountId(rs.getLong("account_id"));
        loan.setPrincipalAmount(rs.getBigDecimal("principal_amount"));
        loan.setRemainingBalance(rs.getBigDecimal("remaining_balance"));
        loan.setInterestRate(rs.getBigDecimal("interest_rate"));
        loan.setTermMonths(rs.getInt("term_months"));
        loan.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        loan.setOverdue(rs.getBoolean("overdue"));
        return loan;
    }
}