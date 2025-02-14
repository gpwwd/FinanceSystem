package com.financialsystem.repository;

import com.financialsystem.domain.Account;
import com.financialsystem.domain.Deposit;
import com.financialsystem.domain.Loan;
import com.financialsystem.mapper.AccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Account> findById(Long id) {
        String sql = "SELECT * FROM account WHERE id = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, new AccountRowMapper(), id);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении аккаунта с id = " + id, e);
        }
    }

    @Transactional
    public Long create(Account account) {
        String sql = "INSERT INTO account (client_id, is_blocked, is_frozen, balance) " +
                "VALUES (?, ?, ?, ?)";
        return executeUpdate(sql, account);
    }

    @Transactional
    public Long update(Account account) {
        String sql = "UPDATE account SET client_id = ?, is_blocked = ?, is_frozen = ?, balance = ? WHERE id = ?";
        return executeUpdate(sql, account);
    }

    private Long executeUpdate(String sql, Account account) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setLong(1, account.getClientId());
                ps.setBoolean(2, account.isBlocked());
                ps.setBoolean(3, account.isFrozen()); // Добавлено поле is_frozen
                ps.setBigDecimal(4, account.getBalance()); // Добавлено поле balance
                if (sql.startsWith("UPDATE")) {
                    ps.setLong(5, account.getId());
                }
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return (keyHolder.getKey() != null) ? keyHolder.getKey().longValue() : null;
    }
}
