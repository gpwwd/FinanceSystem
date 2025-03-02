package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.model.user.Role;
import com.financialsystem.dto.database.user.ClientDatabaseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankingUserDetailsRowMapper implements RowMapper<BankingUserDetails> {
    @Override
    public BankingUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BankingUserDetails(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("passport_series_number"),
                rs.getString("identity_number"),
                rs.getString("phone"),
                rs.getString("email"),
                Role.valueOf(rs.getString("role")),
                rs.getString("password"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
