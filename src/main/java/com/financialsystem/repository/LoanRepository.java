package com.financialsystem.repository;

import com.financialsystem.domain.Account;
import com.financialsystem.domain.Loan;
import com.financialsystem.mapper.LoanRowMapper;
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
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class LoanRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Loan> findById(Long id) {
        String sql = "SELECT * FROM loan WHERE id = ?";
        try {
            Loan loan = jdbcTemplate.queryForObject(sql, new LoanRowMapper(), id);
            return Optional.ofNullable(loan);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении кредита с id = " + id, e);
        }
    }

    public List<Loan> findLoansByAccountId(Long accountId) {
        String sql = "SELECT * FROM loan WHERE account_id = ?";
        return jdbcTemplate.query(sql, new LoanRowMapper(), accountId);
    }

    @Transactional
    public Long create(Loan loan) {
        String sql = "INSERT INTO loan (account_id, principal_amount, remaining_balance, interest_rate," +
                " term_months, created_at, overdue)" +
                " VALUES (?, ?, ?, ?, ? ,?, ?)";
        return executeUpdate(sql, loan);
    }

    @Transactional
    public Long update(Loan loan) {
        String sql = "UPDATE account SET account_id = ?, principal_amount = ?, remaining_balance = ?, " +
                "interest_rate = ?, term_months = ?, created_at = ?, overdue = ? WHERE id = ?";
        return executeUpdate(sql, loan);
    }

    private Long executeUpdate(String sql, Loan loan) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setLong(1, loan.getAccountId());
                ps.setBigDecimal(2, loan.getPrincipalAmount());
                ps.setBigDecimal(3, loan.getRemainingBalance());
                ps.setBigDecimal(4, loan.getInterestRate());
                ps.setInt(5, loan.getTermMonths());
                ps.setTimestamp(6, Timestamp.valueOf(loan.getCreatedAt()));
                ps.setBoolean(7, loan.isOverdue());
                if (sql.startsWith("UPDATE")) {
                    ps.setLong(8, loan.getId());
                }
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return (keyHolder.getKey() != null) ? keyHolder.getKey().longValue() : null;
    }
}
