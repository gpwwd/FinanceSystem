package com.financialsystem.repository.user;

import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.rowMapper.PendingClientRowMapper;
import com.financialsystem.repository.GenericRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class PendingClientRepository extends GenericRepository<PendingClient, PendingClientDatabaseDto> {

    public PendingClientRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO pending_clients (full_name, passport_series_number, identity_number," +
            "phone, email, role, is_foreign, created_at, status, password)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE pending_clients SET full_name = ?, passport_series_number = ?, identity_number = ?," +
            "phone = ?, email = ?, role = ?, is_foreign = ?, created_at = ?, status = ?, password = ? " +
            "WHERE id = ?";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM pending_clients WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM pending_clients WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "select * from pending_clients";
    }

    @Override
    protected RowMapper<PendingClientDatabaseDto> getRowMapper() {
        return new PendingClientRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, PendingClient entity, Connection connection) throws SQLException {
        PendingClientDatabaseDto client = entity.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.startsWith("DELETE")) {
            ps.setLong(1, client.getId());
            return ps;
        }

        ps.setString(1, client.getFullName());
        ps.setString(2, client.getPassport());
        ps.setString(3, client.getIdentityNumber());
        ps.setString(4, client.getPhone());
        ps.setString(5, client.getEmail());
        ps.setString(6, client.getRole().name());
        ps.setBoolean(7, client.isForeign());
        if (sql.startsWith("INSERT")) {
            ps.setTimestamp(8, Timestamp.valueOf(client.getCreatedAt()));
        } else if (sql.startsWith("UPDATE")) {
            ps.setTimestamp(8, getExistingCreatedAt(client.getId()));
        }
        ps.setString(9, client.getStatus().name());
        ps.setString(10, client.getPassword());

        if (sql.startsWith("UPDATE")) {
            ps.setLong(11, client.getId());
        }

        return ps;
    }

    private Timestamp getExistingCreatedAt(Long depositId) {
        String sql = "SELECT created_at FROM pending_clients WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Timestamp.class, depositId);
    }

    @Override
    protected PendingClient fromDto(PendingClientDatabaseDto dto) {
        return PendingClient.fromDto(dto);
    }

    public void approveClient(Long pendingClientId) {
        String sql = "UPDATE pending_clients SET status = 'APPROVED' WHERE id = ?";
        jdbcTemplate.update(sql, pendingClientId);
    }

    public void rejectClient(Long pendingClientId) {
        String sql = "UPDATE pending_clients SET status = 'REJECTED' WHERE id = ?";
        jdbcTemplate.update(sql, pendingClientId);
    }

    private String getFindByNameSql() {
        return "SELECT * FROM pending_clients WHERE full_name = ?";
    }

    public Optional<PendingClient> findByName(String name) {
        String sql = getFindByNameSql();
        try {
            PendingClientDatabaseDto dto = jdbcTemplate.queryForObject(sql, new PendingClientRowMapper(), name);
            return Optional.of(fromDto(dto));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении кредита с name = " + name, e);
        }
    }
}