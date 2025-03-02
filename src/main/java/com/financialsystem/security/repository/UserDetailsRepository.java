package com.financialsystem.security.repository;

import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.rowMapper.BankingUserDetailsRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDetailsRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getFindByFullNameSql() {
        return "SELECT * FROM users WHERE full_name = ?";
    }

    public Optional<BankingUserDetails> findByName(String username) {
        String sql = getFindByFullNameSql();
        try {
            BankingUserDetails bankingUserDetails = jdbcTemplate.queryForObject(sql, new BankingUserDetailsRowMapper(), username);
            return Optional.ofNullable(bankingUserDetails);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении кредита с username = " + username, e);
        }
    }
}
