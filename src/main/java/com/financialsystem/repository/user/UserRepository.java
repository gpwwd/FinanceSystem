package com.financialsystem.repository.user;

import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.Role;
import com.financialsystem.domain.model.user.User;
import com.financialsystem.dto.AccountDatabaseDto;
import com.financialsystem.dto.user.ClientDatabaseDto;
import com.financialsystem.dto.user.UserDatabaseDto;
import com.financialsystem.mapper.ClientRowMapper;
import com.financialsystem.repository.GenericRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UserRepository extends GenericRepository<User, UserDatabaseDto> {

    public UserRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return """
            INSERT INTO users (full_name, passport_series_number, identity_number,
             phone, email, is_foreign, created_at, enterprise_id, role)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
        """;
    }

    @Override
    protected String getUpdateSql() {
        return "";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM users WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM users WHERE id = ?";
    }

    @Override
    protected RowMapper<UserDatabaseDto> getRowMapper() {
        return (rs, rowNum) -> {
            Role role = Role.valueOf(rs.getString("role"));
            return switch (role) {
                case CLIENT -> new ClientRowMapper().mapRow(rs, rowNum);
                default -> throw new IllegalArgumentException("Неизвестная роль: " + role);
            };
        };
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, User entity, Connection connection) throws SQLException {
        UserDatabaseDto dto = entity.toDto();

        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
        ps.setString(1, dto.getFullName());
        ps.setString(2, dto.getPassport());
        ps.setString(3, dto.getIdentityNumber());
        ps.setString(4, dto.getPhone());
        ps.setString(5, dto.getEmail());

        boolean isForeign = (dto instanceof ClientDatabaseDto clientDto) && clientDto.isForeign();
        ps.setBoolean(6, isForeign);
        ps.setTimestamp(7, Timestamp.valueOf(dto.getCreatedAt()));

        if (dto.getEnterpriseId()   != null) {
            ps.setLong(8, dto.getEnterpriseId());
        } else {
            ps.setNull(8, Types.BIGINT);
        }

        ps.setString(9, dto.getRole().name());

        return ps;
    }


    @Override
    protected User fromDto(UserDatabaseDto dto) {
        if (dto instanceof ClientDatabaseDto clientDto) {
            return Client.fromDto(clientDto);
        }
        throw new IllegalArgumentException("Неизвестный тип DTO: " + dto.getClass().getSimpleName());
    }
}
