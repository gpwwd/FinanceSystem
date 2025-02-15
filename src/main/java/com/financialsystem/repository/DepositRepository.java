package com.financialsystem.repository;

import com.financialsystem.domain.Deposit;
import com.financialsystem.mapper.DepositRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class DepositRepository extends GenericRepository<Deposit> {

    @Autowired
    public DepositRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<Deposit> findAll() {
        String sql = "select * from deposit";
        try{
            return jdbcTemplate.query(sql, new DepositRowMapper());
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    public List<Deposit> findDepositsByAccountId(Long accountId) {
        String sql = "SELECT * FROM deposit WHERE account_id = ?";
        return jdbcTemplate.query(sql, new DepositRowMapper(), accountId);
    }


    @Override
    protected String getCreateSql() {
        return "INSERT INTO deposit (balance, account_id, is_blocked, is_frozen, interest_rate, " +
                "created_at, term_months, last_interest_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE deposit SET balance = ?, account_id = ?, is_blocked = ?, is_frozen = ?, interest_rate = ?, " +
                "created_at = ?, term_months = ?, last_interest_date = ? WHERE id = ?";
    }

    @Override
    protected String getFindByIdSql() {
        return "select * from deposit where id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM deposit WHERE id = ?";
    }

    @Override
    protected RowMapper<Deposit> getRowMapper() {
        return new DepositRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Deposit deposit, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.startsWith("DELETE")) {
            return prepareDeleteStatement(ps, deposit);
        }

        prepareCommonFields(ps, deposit);

        if (sql.startsWith("INSERT")) {
            ps.setTimestamp(6, Timestamp.valueOf(deposit.getCreatedAt()));
        } else if (sql.startsWith("UPDATE")) {
            ps.setTimestamp(6, getExistingCreatedAt(deposit.getId()));
        }

        ps.setInt(7, deposit.getTermMonths());
        ps.setTimestamp(8, Timestamp.valueOf(deposit.getLastInterestDate()));

        if (sql.startsWith("UPDATE")) {
            ps.setLong(9, deposit.getId());
        }

        return ps;
    }

    private Timestamp getExistingCreatedAt(Long depositId) {
        String sql = "SELECT created_at FROM deposit WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Timestamp.class, depositId);
    }

    private PreparedStatement prepareDeleteStatement(PreparedStatement ps, Deposit deposit) throws SQLException {
        ps.setLong(1, deposit.getId());
        return ps;
    }

    private void prepareCommonFields(PreparedStatement ps, Deposit deposit) throws SQLException {
        ps.setBigDecimal(1, deposit.getBalance());
        ps.setLong(2, deposit.getAccountId());
        ps.setBoolean(3, deposit.isBlocked());
        ps.setBoolean(4, deposit.isFrozen());
        ps.setBigDecimal(5, deposit.getInterestRate());
    }
}
