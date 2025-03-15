package com.financialsystem.repository.account;

import com.financialsystem.domain.model.account.Account;
import com.financialsystem.domain.model.account.SalaryAccount;
import com.financialsystem.dto.database.account.AccountDatabaseDto;
import com.financialsystem.dto.database.account.SalaryAccountDatabaseDto;
import com.financialsystem.repository.GenericRepository;
import com.financialsystem.rowMapper.AccountRowMapper;
import com.financialsystem.rowMapper.SalaryAccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class AccountRepository extends GenericRepository<Account, Account> {

    @Autowired
    public AccountRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO account (balance, status, owner_id, bank_id, currency, enterprise_id, " +
                "created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE account SET balance = ?, status = ?, owner_id = ?, bank_id = ?, currency = ?, enterprise_id = ?, " +
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
        if (accountDto.getOwnerId() != null) {
            ps.setLong(3, accountDto.getOwnerId());
        } else {
            ps.setNull(3, Types.BIGINT);
        }
        ps.setLong(4, accountDto.getBankId());
        ps.setString(5, accountDto.getCurrency().name());
        if (accountDto.getEnterpriseId() != null) {
            ps.setLong(6, accountDto.getEnterpriseId());
        } else {
            ps.setNull(6, Types.BIGINT);
        }
        ps.setTimestamp(7, Timestamp.valueOf(accountDto.getCreatedAt()));
        if (sql.startsWith("UPDATE")) {
            ps.setLong(8, accountDto.getId());
        }
        return ps;
    }

    @Override
    protected Account fromDto(Account dto) {
        return dto;
    }

    public List<Account> findAllByOwnerId(Long ownerId) {
        String sql = """
            SELECT * FROM account WHERE owner_id = ?;
        """;

        try {
            List<Account> dtos = jdbcTemplate.query(sql, new AccountRowMapper(), ownerId);
            return dtos.stream().map(this::fromDto).toList();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при поиске счета с ownerId = " + ownerId, e);
        }
    }
}
