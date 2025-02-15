package com.financialsystem.mapper;

import com.financialsystem.domain.Deposit;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositRowMapper implements RowMapper<Deposit> {
    @Override
    public Deposit mapRow(ResultSet rs, int rowNum) throws SQLException {
        Deposit deposit = new Deposit();
        deposit.setId(rs.getLong("id"));
        deposit.setBalance(rs.getBigDecimal("balance"));
        deposit.setAccountId(rs.getLong("account_id"));
        deposit.setBlocked(rs.getBoolean("is_blocked"));
        deposit.setFrozen(rs.getBoolean("is_frozen"));
        deposit.setInterestRate(rs.getBigDecimal("interest_rate"));
        deposit.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        deposit.setTermMonths(rs.getInt("term_months"));
        deposit.setLastInterestDate(rs.getTimestamp("last_interest_date").toLocalDateTime());
        return deposit;
    }
}
