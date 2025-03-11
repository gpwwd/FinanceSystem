package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.dto.database.project.SalaryProjectDatabaseDto;
import com.financialsystem.domain.status.SalaryProjectStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryProjectRowMapper implements RowMapper<SalaryProjectDatabaseDto> {
    @Override
    public SalaryProjectDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SalaryProjectDatabaseDto(
                rs.getLong("id"),
                rs.getLong("enterprise_id"),
                rs.getLong("bank_id"),
                Currency.valueOf(rs.getString("currency")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                SalaryProjectStatus.valueOf(rs.getString("status"))
        );
    }
}
