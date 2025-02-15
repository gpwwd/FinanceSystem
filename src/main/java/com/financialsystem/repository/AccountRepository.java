package com.financialsystem.repository;

import com.financialsystem.domain.Account;
import com.financialsystem.mapper.AccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class AccountRepository extends GenericRepository<Account>{

    @Autowired
    public AccountRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO account (client_id, is_blocked, is_frozen, balance) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE account SET client_id = ?, is_blocked = ?, is_frozen = ?, balance = ? WHERE id = ?";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM account WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM account WHERE id = ?";
    }

    @Override
    protected RowMapper<Account> getRowMapper() {
        return new AccountRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Account account, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
        if(sql.startsWith("DELETE")){
            ps.setLong(1, account.getId());
        }
        ps.setLong(1, account.getClientId());
        ps.setBoolean(2, account.isBlocked());
        ps.setBoolean(3, account.isFrozen());
        ps.setBigDecimal(4, account.getBalance());
        if (sql.startsWith("UPDATE")) {
            ps.setLong(5, account.getId());
        }
        return ps;
    }
}
