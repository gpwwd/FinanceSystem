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
        deposit.setAccountNumber(rs.getString("account_number"));
        deposit.setBlocked(rs.getBoolean("is_blocked"));
        deposit.setFrozen(rs.getBoolean("is_frozen"));
        deposit.setAnnualInterestRate(rs.getFloat("annual_interest_rate"));
        return deposit;
    }
}
