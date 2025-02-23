package com.financialsystem.repository;

import com.financialsystem.domain.model.Deposit;
import com.financialsystem.dto.DepositDatabseDto;
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
public class DepositRepository extends GenericRepository<Deposit, Deposit> {

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

    public void batchUpdate(List<Deposit> deposits) {
        String sql = getUpdateSql();
        List<DepositDatabseDto> depositDtos = deposits.stream().map(Deposit::toDto).toList();
        jdbcTemplate.batchUpdate(sql, depositDtos, depositDtos.size(),
                (ps, deposit) -> {
                    ps.setBigDecimal(1, deposit.getBalance());
                    ps.setLong(2, deposit.getAccountId());
                    ps.setString(3, deposit.getDepositStatus().name());
                    ps.setBigDecimal(4, deposit.getInterestRate());
                    ps.setBigDecimal(5, deposit.getPrincipalBalance());
                    ps.setTimestamp(6, Timestamp.valueOf(deposit.getCreatedAt()));
                    ps.setInt(7, deposit.getTermMonths());
                    ps.setTimestamp(8, Timestamp.valueOf(deposit.getLastInterestDate()));
                    ps.setLong(9, deposit.getId());
                }
        );
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO deposit (balance, account_id, deposit_status, interest_rate, principal_balance, " +
                "created_at, term_months, last_interest_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE deposit SET balance = ?, account_id = ?, deposit_status = ?, interest_rate = ?, " +
                "principal_balance = ?, created_at = ?, term_months = ?, last_interest_date = ? WHERE id = ?";
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
        DepositDatabseDto depositDto = deposit.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.startsWith("DELETE")) {
            return prepareDeleteStatement(ps, depositDto);
        }

        prepareCommonFields(ps, depositDto);

        if (sql.startsWith("INSERT")) {
            ps.setTimestamp(6, Timestamp.valueOf(depositDto.getCreatedAt()));
        } else if (sql.startsWith("UPDATE")) {
            ps.setTimestamp(6, getExistingCreatedAt(depositDto.getId()));
        }

        ps.setInt(7, depositDto.getTermMonths());
        ps.setTimestamp(8, Timestamp.valueOf(depositDto.getLastInterestDate()));

        if (sql.startsWith("UPDATE")) {
            ps.setLong(9, depositDto.getId());
        }

        return ps;
    }

    @Override
    protected Deposit fromDto(Deposit dto) {
        return dto;
    }

    private Timestamp getExistingCreatedAt(Long depositId) {
        String sql = "SELECT created_at FROM deposit WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Timestamp.class, depositId);
    }

    private PreparedStatement prepareDeleteStatement(PreparedStatement ps, DepositDatabseDto deposit) throws SQLException {
        ps.setLong(1, deposit.getId());
        return ps;
    }

    private void prepareCommonFields(PreparedStatement ps, DepositDatabseDto deposit) throws SQLException {
        ps.setBigDecimal(1, deposit.getBalance());
        ps.setLong(2, deposit.getAccountId());
        ps.setString(3, deposit.getDepositStatus().name());
        ps.setBigDecimal(4, deposit.getInterestRate());
        ps.setBigDecimal(5, deposit.getPrincipalBalance());
    }
}
