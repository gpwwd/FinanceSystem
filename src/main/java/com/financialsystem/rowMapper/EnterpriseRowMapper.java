package com.financialsystem.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import com.financialsystem.dto.database.EnterpriseDatabaseDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnterpriseRowMapper implements RowMapper<EnterpriseDatabaseDto> {
    @Override
    public EnterpriseDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long payrollAccountId = rs.getLong("payroll_account_id");
        if (rs.wasNull()) {
            payrollAccountId = null;
        }

        return new EnterpriseDatabaseDto(
                rs.getLong("id"),
                rs.getString("type"),
                rs.getString("legal_name"),
                rs.getString("unp"),
                rs.getLong("bank_id"),
                rs.getString("legal_address"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                payrollAccountId
        );
    }
}
