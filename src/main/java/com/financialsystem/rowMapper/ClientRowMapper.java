package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.user.Role;
import com.financialsystem.dto.database.user.ClientDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientRowMapper implements RowMapper<ClientDatabaseDto> {
    @Override
    public ClientDatabaseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Array bankIdsArray = rs.getArray("bank_ids");
        List<Long> bankIds = Collections.emptyList();

        if (bankIdsArray != null) {
            Object arrayObj = bankIdsArray.getArray();
            if (arrayObj instanceof Integer[] bankIdsRaw) {
                bankIds = Arrays.stream(bankIdsRaw)
                        .map(Integer::longValue)
                        .toList();
            }
        }


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
                bankIds // Добавляем список банков
        );
    }
}
