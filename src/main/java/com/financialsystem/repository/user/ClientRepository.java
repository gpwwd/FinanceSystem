package com.financialsystem.repository.user;

import com.financialsystem.domain.model.user.Client;
import com.financialsystem.dto.database.user.ClientDatabaseDto;
import com.financialsystem.rowMapper.ClientRowMapper;
import com.financialsystem.repository.GenericRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class ClientRepository extends GenericRepository<Client, ClientDatabaseDto> {

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return """
            WITH bank_check AS (
                SELECT id FROM bank WHERE id = ?
            ),
            inserted_client AS (
                INSERT INTO users (full_name, passport_series_number, identity_number, phone, email, role, is_foreign, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
            )
            INSERT INTO user_bank (user_id, bank_id)
            SELECT inserted_client.id, bank_check.id
            FROM inserted_client, bank_check
            RETURNING user_id;
        """;
    }

    @Override
    protected String getUpdateSql() {
        return "";
    }

    @Override
    protected String getFindByIdSql() {
        return """
        SELECT 
            u.*, 
            COALESCE(ARRAY_AGG(ub.bank_id), '{}') AS bank_ids
        FROM users u
        LEFT JOIN user_bank ub ON u.id = ub.user_id
        WHERE u.id = ?
        GROUP BY u.id
    """;
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM users WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return """
        SELECT 
            u.*, 
            COALESCE(ARRAY_AGG(ub.bank_id), '{}') AS bank_ids
        FROM users u
        LEFT JOIN user_bank ub ON u.id = ub.user_id
        GROUP BY u.id
    """;
    }


    @Override
    protected RowMapper<ClientDatabaseDto> getRowMapper() {
        return new ClientRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Client entity, Connection connection) throws SQLException {
        if (entity.getClass() != Client.class) {
            throw new IllegalArgumentException("Нельзя передавать наследников класса Client");
        }
        ClientDatabaseDto user = entity.toDto();

        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
        ps.setLong(1, user.getBanksIds().get(0));
        ps.setString(2, user.getFullName());
        ps.setString(3, user.getPassport());
        ps.setString(4, user.getIdentityNumber());
        ps.setString(5, user.getPhone());
        ps.setString(6, user.getEmail());
        ps.setString(7, user.getRole().name());
        ps.setBoolean(8, user.isForeign());
        ps.setTimestamp(9, Timestamp.valueOf(user.getCreatedAt()));

        return ps;
    }

    public void assignEnterprises(Long clientId, List<Long> enterpriseIds) {
        String deleteSql = "DELETE FROM user_enterprise WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, clientId);

        String insertSql = "INSERT INTO user_enterprise (user_id, enterprise_id) VALUES (?, ?)";
        for (Long enterpriseId : enterpriseIds) {
            jdbcTemplate.update(insertSql, clientId, enterpriseId);
        }
    }

    @Override
    protected Client fromDto(ClientDatabaseDto dto) {
        return Client.fromDto(dto);
    }
}
