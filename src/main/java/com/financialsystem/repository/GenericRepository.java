package com.financialsystem.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public abstract class GenericRepository<Entity, Dto> {
    protected final JdbcTemplate jdbcTemplate;

    public GenericRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract String getCreateSql();
    protected abstract String getUpdateSql();
    protected abstract String getFindByIdSql();
    protected abstract String getDeleteSql();
    protected abstract RowMapper<Dto> getRowMapper();
    protected abstract PreparedStatement createPreparedStatement(String sql, Entity entity, Connection connection) throws SQLException;
    protected abstract Entity fromDto(Dto dto);

    @Transactional
    public Long create(Entity entity) {
        return execute(getCreateSql(), entity);
    }

    @Transactional
    public Long update(Entity entity) {
        return execute(getUpdateSql(), entity);
    }

    @Transactional
    public Long delete(Entity entity) {
        return execute(getDeleteSql(), entity);
    }

    public Optional<Entity> findById(Long id) {
        String sql = getFindByIdSql();
        try {
            Dto entity = jdbcTemplate.queryForObject(sql, getRowMapper(), id);
            return Optional.of(fromDto(entity));
            //return Optional.ofNullable(entity);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении кредита с id = " + id, e);
        }
    }

    private Long execute(String sql, Entity entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> createPreparedStatement(sql, entity, connection), keyHolder);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении операции с сущностью" + entity.getClass().getSimpleName(), e);
        }

        return (keyHolder.getKey() != null) ? keyHolder.getKey().longValue() : null;
    }
}
