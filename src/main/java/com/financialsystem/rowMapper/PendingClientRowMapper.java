package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.user.PendingClientStatus;
import com.financialsystem.domain.model.user.Role;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PendingClientRowMapper implements RowMapper<PendingClientDatabaseDto> {
    @Override
    public PendingClientDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new PendingClientDatabaseDto(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("passport_series_number"),
                rs.getString("identity_number"),
                rs.getString("phone"),
                rs.getString("email"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_foreign"),
                PendingClientStatus.valueOf(rs.getString("status")),
                rs.getLong("bank_id")
        );
    }
}
