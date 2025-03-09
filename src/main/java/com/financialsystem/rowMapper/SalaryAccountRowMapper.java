package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.dto.database.account.SalaryAccountDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryAccountRowMapper implements RowMapper<SalaryAccountDatabaseDto> {

    @Override
    public SalaryAccountDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
       return new SalaryAccountDatabaseDto(
               rs.getLong("id"),
               AccountStatus.valueOf(rs.getString("status")),
               rs.getLong("owner_id"),
               rs.getLong("bank_id"),
               Currency.valueOf(rs.getString("currency")),
               rs.getTimestamp("created_at").toLocalDateTime(),
               rs.getBigDecimal("balance"),
               rs.getLong("salary_project_id")
       );
    }
}
