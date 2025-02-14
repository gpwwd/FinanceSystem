package com.financialsystem.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class GenericRepository<T> {
    protected final JdbcTemplate jdbcTemplate;

    public GenericRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract String getCreateSql();
    protected abstract String getUpdateSql();
    protected abstract PreparedStatement createPreparedStatement(String sql, T entity, Connection connection) throws SQLException;

    @Transactional
    public Long create(T entity) {
        return executeUpdate(getCreateSql(), entity);
    }

    @Transactional
    public Long update(T entity) {
        return executeUpdate(getUpdateSql(), entity);
    }

    private Long executeUpdate(String sql, T entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> createPreparedStatement(sql, entity, connection), keyHolder);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении операции с сущностью" + entity.getClass().getSimpleName(), e);
        }

        return (keyHolder.getKey() != null) ? keyHolder.getKey().longValue() : null;
    }
}
