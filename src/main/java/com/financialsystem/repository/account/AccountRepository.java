package com.financialsystem.repository.account;

import com.financialsystem.domain.model.account.Account;
import com.financialsystem.dto.database.account.AccountDatabaseDto;
import com.financialsystem.repository.GenericRepository;
import com.financialsystem.rowMapper.AccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Repository
public class AccountRepository extends GenericRepository<Account, Account> {

    @Autowired
    public AccountRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO account (balance, status, owner_id, bank_id, currency, " +
                "created_at) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE account SET balance = ?, status = ?, owner_id = ?, bank_id = ?, currency = ?, " +
                "created_at = ? WHERE id = ?";
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
    protected String getFindAllSql() {
        return "select * from account";
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
            return ps;
        }
        ps.setBigDecimal(1, accountDto.getBalance());
        ps.setString(2, accountDto.getStatus().name());
        ps.setLong(3, accountDto.getOwnerId());
        ps.setLong(4, accountDto.getBankId());
        ps.setString(5, accountDto.getCurrency().name());
        ps.setTimestamp(6, Timestamp.valueOf(accountDto.getCreatedAt()));
        if (sql.startsWith("UPDATE")) {
            ps.setLong(7, accountDto.getId());
        }
        return ps;
    }

    @Override
    protected Account fromDto(Account dto) {
        return dto;
    }
}
