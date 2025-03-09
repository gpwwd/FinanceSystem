package com.financialsystem.repository.loan;

import com.financialsystem.dto.database.loan.PendingLoanDatabaseDto;
import com.financialsystem.repository.GenericRepository;
import com.financialsystem.rowMapper.PendingLoanRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PendingLoanRepository extends GenericRepository<PendingLoanDatabaseDto, PendingLoanDatabaseDto> {

    @Autowired
    public PendingLoanRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO pending_loan (account_id, principal_amount, term_months, request_status," +
                " is_fixed_interest)" +
                " VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE pending_loan SET account_id = ?, principal_amount = ?, term_months = ?, " +
                "request_status = ?, is_fixed_interest = ? WHERE id = ?";
    }

    @Override
    protected String getFindByIdSql() {
        return "SELECT * FROM pending_loan WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM pending_loan WHERE id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "select * from pending_loan";
    }

    @Override
    protected RowMapper<PendingLoanDatabaseDto> getRowMapper() {
        return new PendingLoanRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, PendingLoanDatabaseDto loanDto, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
        if(sql.startsWith("DELETE")){
            ps.setLong(1, loanDto.getId());
            return ps;
        }
        ps.setLong(1, loanDto.getAccountId());
        ps.setBigDecimal(2, loanDto.getPrincipalAmount());
        ps.setInt(3, loanDto.getTermMonths());
        ps.setString(4, loanDto.getRequestStatus().name());
        ps.setBoolean(5, loanDto.isFixedInterest());
        if (sql.startsWith("UPDATE")) {
            ps.setLong(6, loanDto.getId());
        }
        return ps;
    }

    @Override
    protected PendingLoanDatabaseDto fromDto(PendingLoanDatabaseDto pendingLoanDatabaseDto) {
        return pendingLoanDatabaseDto;
    }

    public List<PendingLoanDatabaseDto> findByAccountId(Long accountId) {
        String sql = "SELECT * FROM pending_loan WHERE account_id = ?";
        try {
            return jdbcTemplate.query(sql, getRowMapper(), accountId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении ожидающих открытия кредитов для account_id = " + accountId, e);
        }
    }

}
