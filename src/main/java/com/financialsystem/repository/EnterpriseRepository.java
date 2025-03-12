package com.financialsystem.repository;

import com.financialsystem.domain.model.Enterprise;
import com.financialsystem.dto.database.EnterpriseDatabaseDto;
import com.financialsystem.rowMapper.EnterpriseRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class EnterpriseRepository extends GenericRepository<Enterprise, EnterpriseDatabaseDto> {

    public EnterpriseRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return """
            INSERT INTO enterprise (type, legal_name, unp, bank_id, legal_address, created_at, payroll_account_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
    }

    @Override
    protected String getUpdateSql() {
        return """
            UPDATE enterprise SET type = ?, legal_name = ?, unp = ?,
            bank_id = ?, legal_address = ?, payroll_account_id = ?
            WHERE id = ?
        """;
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM enterprise WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM enterprise WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM enterprise";
    }

    @Override
    protected RowMapper<EnterpriseDatabaseDto> getRowMapper() {
        return new EnterpriseRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Enterprise enterprise, Connection connection) throws SQLException {
        EnterpriseDatabaseDto dto = enterprise.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.contains("UPDATE enterprise SET")) {
            fillPreparedStatement(ps, dto);
            ps.setLong(6, dto.payrollAccountId());
            ps.setLong(7, dto.id());
            return ps;
        }

        if (sql.contains("DELETE FROM enterprise")) {
            ps.setLong(1, dto.id());
            return ps;
        }

        fillPreparedStatement(ps, dto);
        ps.setTimestamp(6, java.sql.Timestamp.valueOf(dto.createdAt()));
        ps.setLong(7, dto.payrollAccountId());
        return ps;
    }

    private void fillPreparedStatement(PreparedStatement ps, EnterpriseDatabaseDto dto) throws SQLException {
        ps.setString(1, dto.type());
        ps.setString(2, dto.legalName());
        ps.setString(3, dto.unp());
        ps.setLong(4, dto.bankId());
        ps.setString(5, dto.legalAddress());
    }

    @Override
    protected Enterprise fromDto(EnterpriseDatabaseDto dto) {
        return Enterprise.fromDto(dto);
    }

    public String getFindByLegalNameSql() {
        return "SELECT * FROM enterprise WHERE legal_name = ?";
    }

    public Optional<EnterpriseDatabaseDto> findByLegalName(String legalName) {
        String sql = getFindByLegalNameSql();
        try {
            EnterpriseDatabaseDto dto = jdbcTemplate.queryForObject(sql, new EnterpriseRowMapper(), legalName);
            return Optional.of(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении предприятия с legalName = " + legalName, e);
        }
    }

    public String getFindByUnpSql() {
        return "SELECT * FROM enterprise WHERE unp = ?";
    }

    public Optional<EnterpriseDatabaseDto> findByUnp(String unp) {
        String sql = getFindByUnpSql();
        try {
            EnterpriseDatabaseDto dto = jdbcTemplate.queryForObject(sql, new EnterpriseRowMapper(), unp);
            return Optional.of(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении предприятия с unp = " + unp, e);
        }
    }
}
