package com.financialsystem.repository.account;

import com.financialsystem.domain.model.account.SalaryAccount;
import com.financialsystem.dto.database.account.SalaryAccountDatabaseDto;
import com.financialsystem.dto.database.project.SalaryProjectDatabaseDto;
import com.financialsystem.dto.database.user.SpecialistDatabaseDto;
import com.financialsystem.repository.GenericRepository;
import com.financialsystem.rowMapper.SalaryAccountRowMapper;
import com.financialsystem.rowMapper.SalaryProjectRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

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
            INSERT INTO account (owner_id, bank_id, currency, status, balance, enterprise_id, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        )
        INSERT INTO salary_account (account_id, salary_project_id, pending_status, salary)
        SELECT id, ?, ?, ? FROM inserted_account
        RETURNING id;
    """;
    }


    @Override
    protected String getUpdateSql() {
        return """
        WITH updated_account AS (
            UPDATE account
            SET owner_id = ?, bank_id = ?, currency = ?, status = ?, balance = ?, enterprise_id = ?
            WHERE id = (SELECT account_id FROM salary_account WHERE id = ?)
            RETURNING id
        )
        UPDATE salary_account
        SET salary_project_id = ?, pending_status = ?, salary = ?
        WHERE id = ?;
    """;
    }

    @Override
    protected String getFindByIdSql() {
        return """
        SELECT sa.salary_project_id, sa.pending_status, sa.salary, a.*
        FROM salary_account sa
        JOIN account a ON sa.account_id = a.id
        WHERE a.id = ?;
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
        SELECT sa.id, sa.account_id, sa.salary_project_id, sa.pending_status, sa.salary,
               a.owner_id, a.bank_id, a.currency, a.created_at, a.balance, a.status, a.enterprise_id
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
            ps.setLong(7, dto.getId());
            ps.setLong(8, dto.getSalaryProjectId());
            ps.setLong(11, dto.getId());
            return ps;
        }

        fillPreparedStatement(ps, dto);
        ps.setTimestamp(7, Timestamp.valueOf(dto.getCreatedAt()));
        ps.setLong(8, dto.getSalaryProjectId());
        return ps;
    }

    private void fillPreparedStatement(PreparedStatement ps, SalaryAccountDatabaseDto dto) throws SQLException {
        if (dto.getOwnerId() != null) {
            ps.setLong(1, dto.getOwnerId());
        } else {
            ps.setNull(1, Types.BIGINT);
        }

        ps.setLong(2, dto.getBankId());
        ps.setString(3, dto.getCurrency().name());
        ps.setString(4, dto.getStatus().name());
        ps.setBigDecimal(5, dto.getBalance());

        if (dto.getEnterpriseId() != null) {
            ps.setLong(6, dto.getEnterpriseId());
        } else {
            ps.setNull(6, Types.BIGINT);
        }

        ps.setString(9, dto.getSalaryAccountStatus().name());
        ps.setBigDecimal(10, dto.getSalaryAmount());
    }

    public List<SalaryAccount> findAllBySalaryProjectId(Long salaryProjectId) {
        String sql = """
            SELECT sa.id, sa.account_id, sa.salary_project_id, sa.pending_status, sa.salary,
                   a.owner_id, a.bank_id, a.currency, a.created_at, a.balance, a.status, a.enterprise_id
            FROM salary_account sa
            JOIN account a ON sa.account_id = a.id
            WHERE sa.salary_project_id = ?;
        """;

        try {
            List<SalaryAccountDatabaseDto> dtos = jdbcTemplate.query(sql, new SalaryAccountRowMapper(), salaryProjectId);
            return dtos.stream().map(this::fromDto).toList();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при поиске зарплатного проекта с salaryProjectId = " + salaryProjectId, e);
        }
    }

    public Long findAccountIdById(Long salaryAccountId) {
        String sql = "SELECT account_id FROM salary_account WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, Long.class, salaryAccountId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при поиске account_id по salary_account.id = " + salaryAccountId, e);
        }
    }


    @Override
    protected SalaryAccount fromDto(SalaryAccountDatabaseDto dto) {
        return SalaryAccount.fromDto(dto);
    }
}
