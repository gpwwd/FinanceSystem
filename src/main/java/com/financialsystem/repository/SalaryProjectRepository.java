package com.financialsystem.repository;

import com.financialsystem.domain.model.SalaryProject;
import com.financialsystem.dto.database.BankDatabaseDto;
import com.financialsystem.dto.database.EnterpriseDatabaseDto;
import com.financialsystem.dto.database.SalaryProjectDatabaseDto;
import com.financialsystem.rowMapper.BankRowMapper;
import com.financialsystem.rowMapper.EnterpriseRowMapper;
import com.financialsystem.rowMapper.SalaryProjectRowMapper;
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
public class SalaryProjectRepository extends GenericRepository<SalaryProject, SalaryProjectDatabaseDto> {

    public SalaryProjectRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO salary_project (enterprise_id, bank_id, created_at, status, currency) VALUES (?, ?, ?, ?, ?) RETURNING id";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE salary_project SET enterprise_id = ?, bank_id = ?, created_at = ?, status = ?, currency = ? WHERE id = ?";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM salary_project WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM salary_project WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM salary_project";
    }

    @Override
    protected RowMapper<SalaryProjectDatabaseDto> getRowMapper() {
        return new SalaryProjectRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, SalaryProject salaryProject, Connection connection) throws SQLException {
        SalaryProjectDatabaseDto dto = salaryProject.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.contains("UPDATE salary_project SET")) {
            fillPreparedStatement(ps, dto);
            ps.setLong(6, dto.getId());
            return ps;
        }

        if (sql.contains("DELETE FROM salary_project")) {
            ps.setLong(1, dto.getId());
            return ps;
        }

        fillPreparedStatement(ps, dto);
        return ps;
    }

    private void fillPreparedStatement(PreparedStatement ps, SalaryProjectDatabaseDto dto) throws SQLException {
        ps.setLong(1, dto.getEnterpriseId());
        ps.setLong(2, dto.getBankId());
        ps.setObject(3, dto.getCreatedAt());
        ps.setString(4, dto.getStatus().name());
        ps.setString(5, dto.getCurrency().name());
    }

    @Override
    protected SalaryProject fromDto(SalaryProjectDatabaseDto dto) {
        return SalaryProject.fromDto(dto);
    }

    public Optional<SalaryProject> findByEnterpriseId(Long enterpriseId) {
        String sql = "SELECT * FROM salary_project WHERE enterprise_id = ?";
        try {
            SalaryProjectDatabaseDto dto = jdbcTemplate.queryForObject(sql, new SalaryProjectRowMapper(), enterpriseId);
            return Optional.of(fromDto(dto));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при поиске зарплатного проекта с enterpriseId = " + enterpriseId, e);
        }
    }
}
