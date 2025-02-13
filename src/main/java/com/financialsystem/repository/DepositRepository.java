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

@Repository
public class DepositRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DepositRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Deposit> findAll() {
        String sql = "select * from deposits";
        try{
            return jdbcTemplate.query(sql, new DepositRowMapper());
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    public Deposit findById(Long id) {
        String sql = "select * from deposits where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new DepositRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Депозит с id = " + id + " не найден", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении депозита с id = " + id, e);
        }
    }

    @Transactional
    public Long create(Deposit deposit) {
        String sql = "INSERT INTO deposits (balance, account_number, is_blocked, is_frozen, annual_interest_rate)" +
                " VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql, deposit);
    }

    @Transactional
    public Long update(Deposit deposit) {
        String sql = "UPDATE deposits SET balance = ?, account_number = ?, is_blocked = ?, is_frozen = ?," +
                " annual_interest_rate = ? WHERE id = ?";
        return executeUpdate(sql, deposit);
    }

    private Long executeUpdate(String sql, Deposit deposit) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setBigDecimal(1, deposit.getBalance());
                ps.setString(2, deposit.getAccountNumber());
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
