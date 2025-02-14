package com.financialsystem.repository;

import com.financialsystem.domain.Deposit;
import com.financialsystem.mapper.DepositRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class DepositRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DepositRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Deposit> findAll() {
        String sql = "select * from deposit";
        try{
            return jdbcTemplate.query(sql, new DepositRowMapper());
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Deposit> findById(Long id) {
        String sql = "select * from deposit where id = ?";
        try {
            Deposit deposit = jdbcTemplate.queryForObject(sql, new DepositRowMapper(), id);
            return Optional.ofNullable(deposit);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении депозита с id = " + id, e);
        }
    }

    public List<Deposit> findDepositsByAccountId(Long accountId) {
        String sql = "SELECT * FROM deposit WHERE account_id = ?";
        return jdbcTemplate.query(sql, new DepositRowMapper(), accountId);
    }

    @Transactional
    public Long create(Deposit deposit) {
        String sql = "INSERT INTO deposit (balance, account_id, is_blocked, is_frozen, annual_interest_rate)" +
                " VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql, deposit);
    }

    @Transactional
    public Long update(Deposit deposit) {
        String sql = "UPDATE deposit SET balance = ?, account_id = ?, is_blocked = ?, is_frozen = ?," +
                " annual_interest_rate = ? WHERE id = ?";
        return executeUpdate(sql, deposit);
    }

    private Long executeUpdate(String sql, Deposit deposit) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setBigDecimal(1, deposit.getBalance());
                ps.setLong(2, deposit.getAccountId());
                ps.setBoolean(3, deposit.isBlocked());
                ps.setBoolean(4, deposit.isFrozen());
                ps.setDouble(5, deposit.getAnnualInterestRate());
                if (sql.startsWith("UPDATE")) {
                    ps.setLong(6, deposit.getId());
                }
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return (keyHolder.getKey() != null) ? keyHolder.getKey().longValue() : null;
    }
}
