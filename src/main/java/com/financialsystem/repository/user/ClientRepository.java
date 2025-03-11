package com.financialsystem.repository.user;

import com.financialsystem.domain.model.user.Client;
import com.financialsystem.dto.database.user.ClientDatabaseDto;
import com.financialsystem.rowMapper.ClientRowMapper;
import com.financialsystem.repository.GenericRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class ClientRepository extends GenericRepository<Client, ClientDatabaseDto> {

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO users (full_name, passport_series_number, identity_number, " +
                "phone, email, role, is_foreign, created_at, password)" +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" +
        "RETURNING id";
    }

    @Override
    protected String getUpdateSql() {
        return "";
    }

    @Override
    protected String getFindByIdSql() {
        return """
        SELECT * FROM users WHERE id = ?;
        """;
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM users WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return """
        SELECT * FROM users;
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

        if (sql.startsWith("DELETE")) {
            ps.setLong(1, user.getId());
            return ps;
        }

        ps.setString(1, user.getFullName());
        ps.setString(2, user.getPassport());
        ps.setString(3, user.getIdentityNumber());
        ps.setString(4, user.getPhone());
        ps.setString(5, user.getEmail());
        ps.setString(6, user.getRole().name());
        ps.setBoolean(7, user.isForeign());
        if (sql.startsWith("INSERT")) {
            ps.setTimestamp(8, Timestamp.valueOf(user.getCreatedAt()));
        } else if (sql.startsWith("UPDATE")) {
            ps.setTimestamp(8, getExistingCreatedAt(user.getId()));
        }
        ps.setString(9, user.getPassword());

        if (sql.startsWith("UPDATE")) {
            ps.setLong(10, user.getId());
        }

        return ps;
    }

    private Timestamp getExistingCreatedAt(Long depositId) {
        String sql = "SELECT created_at FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Timestamp.class, depositId);
    }

    @Override
    protected Client fromDto(ClientDatabaseDto dto) {
        return Client.fromDto(dto);
    }

    public Optional<Client> findByPassportSeriesNumber(String passpoprtSeriesNumber) {
        String sql = "SELECT * FROM users WHERE passport_series_number = ?;";
        try {
            ClientDatabaseDto dto = jdbcTemplate.queryForObject(sql, new ClientRowMapper(), passpoprtSeriesNumber);
            return Optional.of(fromDto(dto));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при поиске клиента с серийным номером пасспорта = " + passpoprtSeriesNumber, e);
        }
    }
}
