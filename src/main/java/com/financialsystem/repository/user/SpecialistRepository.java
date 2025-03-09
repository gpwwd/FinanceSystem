package com.financialsystem.repository.user;

import com.financialsystem.domain.model.user.Specialist;
import com.financialsystem.dto.database.user.SpecialistDatabaseDto;
import com.financialsystem.repository.GenericRepository;
import com.financialsystem.rowMapper.SpecialistRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
                                               phone, email, role, created_at, password)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                            RETURNING id
                        )
                        INSERT INTO specialists (user_id, enterprise_id)\s
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
            WHERE id = (SELECT user_id FROM specialists WHERE id = ?);
        """;
    }

    @Override
    protected String getFindByIdSql() {
        return """
            SELECT u.*, es.enterprise_id FROM users u
            JOIN specialists es ON u.id = es.user_id
            WHERE es.id = ?;
        """;
    }

    @Override
    protected String getDeleteSql() {
        return """
            WITH deleted_specialist AS (
                DELETE FROM specialists WHERE id = ? RETURNING user_id
            )
            DELETE FROM users WHERE id IN (SELECT user_id FROM deleted_specialist);
        """;
    }

    @Override
    protected String getFindAllSql() {
        return """
            SELECT u.*, es.enterprise_id FROM users u
            JOIN specialists es ON u.id = es.user_id;
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

        if (sql.contains("DELETE FROM specialists")) {
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

        ps = connection.prepareStatement(sql, new String[]{"id"});
        fillPreparedStatement(ps, dto);
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
        throw new UnsupportedOperationException();
    }
}
