package com.financialsystem.rowMapper;

import com.financialsystem.dto.database.BankDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankRowMapper implements RowMapper<BankDatabaseDto> {
    @Override
    public BankDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BankDatabaseDto(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("bik"),
                rs.getString("address")
        );
    }
}