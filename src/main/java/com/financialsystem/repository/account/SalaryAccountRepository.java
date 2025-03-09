package com.financialsystem.repository.account;

import com.financialsystem.domain.model.account.SalaryAccount;
import com.financialsystem.dto.database.account.SalaryAccountDatabaseDto;
import com.financialsystem.dto.database.user.SpecialistDatabaseDto;
import com.financialsystem.repository.GenericRepository;
import com.financialsystem.rowMapper.SalaryAccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Repository
public class SalaryAccountRepository extends GenericRepository<SalaryAccount, SalaryAccountDatabaseDto> {

    @Autowired
    public SalaryAccountRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return """
            WITH inserted_account AS (
                INSERT INTO account (owner_id, bank_id, currency, status, balance, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
            )
            INSERT INTO salary_account (account_id, salary_project_id)
            SELECT id, ? FROM inserted_account
            RETURNING id;
        """;
    }

    @Override
    protected String getUpdateSql() {
        return """
            UPDATE account
            SET owner_id = ?, bank_id = ?, currency = ?, status = ?, balance = ?
            WHERE id = (SELECT account_id FROM salary_account WHERE id = ?);
        """;
    }

    @Override
    protected String getFindByIdSql() {
        return """
            SELECT sa.salary_project_id, a.* FROM salary_account sa
            JOIN account a ON sa.account_id = a.id
            WHERE sa.id = ?;
        """;
    }

    @Override
    protected String getDeleteSql() {
        return """
            WITH deleted_salary_account AS (
                DELETE FROM salary_account WHERE id = ? RETURNING account_id
            )
            DELETE FROM account WHERE id IN (SELECT account_id FROM deleted_salary_account);
        """;
    }

    @Override
    protected String getFindAllSql() {
        return """
            SELECT sa.id, sa.account_id, sa.salary_project_id, a.owner_id, a.bank_id, a.currency, a.created_at, a.balance, a.status
            FROM salary_account sa
            JOIN account a ON sa.account_id = a.id;
        """;
    }

    @Override
    protected RowMapper<SalaryAccountDatabaseDto> getRowMapper() {
        return new SalaryAccountRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, SalaryAccount salaryAccount, Connection connection) throws SQLException {
        SalaryAccountDatabaseDto dto = salaryAccount.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.contains("DELETE FROM salary_account")) {
            ps.setLong(1, dto.getId());
            return ps;
        }

        if (sql.contains("UPDATE account")) {
            ps = connection.prepareStatement(sql);
            fillPreparedStatement(ps, dto);
            ps.setLong(6, dto.getId());
            return ps;
        }

        fillPreparedStatement(ps, dto);
        ps.setTimestamp(6, Timestamp.valueOf(dto.getCreatedAt()));
        ps.setLong(7, dto.getSalaryProjectId());
        return ps;
    }

    private void fillPreparedStatement(PreparedStatement ps, SalaryAccountDatabaseDto dto) throws SQLException {
        ps.setLong(1, dto.getOwnerId());
        ps.setLong(2, dto.getBankId());
        ps.setString(3, dto.getCurrency().name());
        ps.setString(4, dto.getStatus().name());
        ps.setBigDecimal(5, dto.getBalance());
    }


    @Override
    protected SalaryAccount fromDto(SalaryAccountDatabaseDto dto) {
        return SalaryAccount.fromDto(dto);
    }
}
