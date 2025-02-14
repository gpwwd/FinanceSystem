package com.financialsystem.mapper;

import com.financialsystem.domain.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setClientId(rs.getLong("client_id"));
        account.setBlocked(rs.getBoolean("is_blocked"));
        account.setFrozen(rs.getBoolean("is_frozen"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}