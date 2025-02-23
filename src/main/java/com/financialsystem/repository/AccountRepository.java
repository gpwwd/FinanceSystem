package com.financialsystem.repository;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.dto.AccountDatabaseDto;
import com.financialsystem.dto.LoanDatabaseDto;
import com.financialsystem.mapper.AccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class AccountRepository extends GenericRepository<Account, Account>{

    @Autowired
    public AccountRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO account (client_id, status, balance) VALUES (?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE account SET client_id = ?, status = ?, balance = ?  WHERE id = ?";
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
        AccountDatabaseDto accountDto = account.toDto();

        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
        if(sql.startsWith("DELETE")){
            ps.setLong(1, accountDto.getId());
        }
        ps.setLong(1, accountDto.getClientId());
        ps.setString(2, accountDto.getStatus().name());
        ps.setBigDecimal(3, accountDto.getBalance());
        if (sql.startsWith("UPDATE")) {
            ps.setLong(4, accountDto.getId());
        }
        return ps;
    }

    @Override
    protected Account fromDto(Account dto) {
        return dto;
    }
}
