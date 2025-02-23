package com.financialsystem.mapper;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.Role;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.dto.user.ClientDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.security.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ClientRowMapper implements RowMapper<ClientDatabaseDto> {
    @Override
    public ClientDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ClientDatabaseDto(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("passport_series_number"),
                rs.getString("identity_number"),
                rs.getString("phone"),
                rs.getString("email"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_foreign"),
                rs.getObject("enterprise_id", Long.class)
        );
    }
}
