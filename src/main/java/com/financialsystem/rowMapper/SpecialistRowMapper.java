package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.user.Role;
import com.financialsystem.dto.database.user.SpecialistDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialistRowMapper implements RowMapper<SpecialistDatabaseDto> {
    @Override
    public SpecialistDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SpecialistDatabaseDto(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("passport_series_number"),
                rs.getString("identity_number"),
                rs.getString("phone"),
                rs.getString("email"),
                Role.valueOf(rs.getString("role")),
                rs.getString("password"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getLong("enterprise_id")
        );
    }
}
