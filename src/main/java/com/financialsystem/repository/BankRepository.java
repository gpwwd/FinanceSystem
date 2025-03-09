package com.financialsystem.repository;

import com.financialsystem.domain.model.Bank;
import com.financialsystem.dto.database.BankDatabaseDto;
import com.financialsystem.rowMapper.BankRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class BankRepository extends GenericRepository<Bank, BankDatabaseDto> {

    public BankRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO bank (name, bik, address) VALUES (?, ?, ?) RETURNING id";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE bank SET name = ?, bik = ?, address = ? WHERE id = ?";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM bank WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM bank WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "SELECT * FROM bank";
    }

    @Override
    protected RowMapper<BankDatabaseDto> getRowMapper() {
        return new BankRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Bank bank, Connection connection) throws SQLException {
        BankDatabaseDto dto = bank.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.contains("UPDATE bank SET")) {
            fillPreparedStatement(ps, dto);
            ps.setLong(4, dto.getId());
            return ps;
        }

        if (sql.contains("DELETE FROM bank")) {
            ps.setLong(1, dto.getId());
            return ps;
        }

        fillPreparedStatement(ps, dto);
        return ps;
    }

    private void fillPreparedStatement(PreparedStatement ps, BankDatabaseDto dto) throws SQLException {
        ps.setString(1, dto.getName());
        ps.setString(2, dto.getBik());
        ps.setString(3, dto.getAddress());
    }

    @Override
    protected Bank fromDto(BankDatabaseDto dto) {
        return Bank.fromDto(dto);
    }

    public Optional<BankDatabaseDto> findByBik(String bik) {
        String sql = "SELECT * FROM bank WHERE bik = ?";
        try {
            BankDatabaseDto dto = jdbcTemplate.queryForObject(sql, new BankRowMapper(), bik);
            return Optional.of(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении банка с BIK = " + bik, e);
        }
    }
}
