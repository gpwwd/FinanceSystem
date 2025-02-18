package com.financialsystem.mapper;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.domain.status.LoanStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Account(
                rs.getLong("id"),
                AccountStatus.valueOf(rs.getString("status")),
                rs.getLong("client_id"),
                rs.getBigDecimal("balance")
        );
    }
}