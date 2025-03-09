package com.financialsystem.repository.user;

import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.domain.model.user.Specialist;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.dto.database.user.SpecialistDatabaseDto;
import com.financialsystem.repository.GenericRepository;
import com.financialsystem.rowMapper.PendingClientRowMapper;
import com.financialsystem.rowMapper.SpecialistRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class SpecialistRepository extends GenericRepository<Specialist, SpecialistDatabaseDto> {

    @Autowired
    public SpecialistRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return """
                WITH inserted_user AS (
                            INSERT INTO users (full_name, passport_series_number, identity_number,\s
                                               phone, email, role, password, created_at)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                            RETURNING id
                        )
                        INSERT INTO specialist (user_id, enterprise_id)\s
                        SELECT id, ? FROM inserted_user\s
                        RETURNING id
                """;
    }

    @Override
    protected String getUpdateSql() {
        return """
            UPDATE users
            SET full_name = ?, passport_series_number = ?, identity_number = ?,
                phone = ?, email = ?, role = ?, password = ?
            WHERE id = (SELECT user_id FROM specialist WHERE id = ?);
        """;
    }

    @Override
    protected String getFindByIdSql() {
        return """
            SELECT u.*, es.enterprise_id FROM users u
            JOIN specialist es ON u.id = es.user_id
            WHERE es.id = ?;
        """;
    }

    @Override
    protected String getDeleteSql() {
        return """
            WITH deleted_specialist AS (
                DELETE FROM specialist WHERE id = ? RETURNING user_id
            )
            DELETE FROM users WHERE id IN (SELECT user_id FROM deleted_specialist);
        """;
    }

    @Override
    protected String getFindAllSql() {
        return """
            SELECT u.*, es.enterprise_id FROM users u
            JOIN specialist es ON u.id = es.user_id;
        """;
    }

    @Override
    protected RowMapper<SpecialistDatabaseDto> getRowMapper() {
        return new SpecialistRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Specialist specialist, Connection connection) throws SQLException {
        SpecialistDatabaseDto dto = specialist.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.contains("DELETE FROM specialist")) {
            ps = connection.prepareStatement(sql);
            ps.setLong(1, dto.getId());
            return ps;
        }

        if (sql.contains("UPDATE users")) {
            ps = connection.prepareStatement(sql);
            fillPreparedStatement(ps, dto);
            ps.setLong(8, dto.getId());
            return ps;
        }

        fillPreparedStatement(ps, dto);
        ps.setTimestamp(8, Timestamp.valueOf(dto.getCreatedAt()));
        ps.setLong(9, dto.getEnterpriseId());

        return ps;
    }

    private void fillPreparedStatement(PreparedStatement ps, SpecialistDatabaseDto dto) throws SQLException {
        ps.setString(1, dto.getFullName());
        ps.setString(2, dto.getPassport());
        ps.setString(3, dto.getIdentityNumber());
        ps.setString(4, dto.getPhone());
        ps.setString(5, dto.getEmail());
        ps.setString(6, dto.getRole().name());
        ps.setString(7, dto.getPassword());
    }

    @Override
    protected Specialist fromDto(SpecialistDatabaseDto specialistDatabaseDto) {
        return Specialist.fromDto(specialistDatabaseDto);
    }

    public String getFindByNameSql() {
        return """
        SELECT u.id, u.full_name, u.passport_series_number, u.identity_number, 
               u.phone, u.email, u.role, u.password, u.created_at, s.enterprise_id
        FROM users u
        JOIN specialist s ON u.id = s.user_id
        WHERE u.full_name = ?;
    """;
    }

    public Optional<SpecialistDatabaseDto> findByName(String name) {
        String sql = getFindByNameSql();
        try {
            SpecialistDatabaseDto dto = jdbcTemplate.queryForObject(sql, new SpecialistRowMapper(), name);
            return Optional.of(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении специалиста стороннего предприятия с name = " + name, e);
        }
    }
}
