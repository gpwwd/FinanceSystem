package com.financialsystem.repository;

import com.financialsystem.domain.Deposit;
import com.financialsystem.domain.Loan;
import com.financialsystem.mapper.DepositRowMapper;
import com.financialsystem.mapper.LoanRowMapper;
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
public class LoanRepository extends GenericRepository<Loan> {

    @Autowired
    public LoanRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<Loan> findAll() {
        String sql = "select * from loan";
        try{
            return jdbcTemplate.query(sql, new LoanRowMapper());
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    public List<Loan> findLoansByAccountId(Long accountId) {
        String sql = "SELECT * FROM loan WHERE account_id = ?";
        return jdbcTemplate.query(sql, new LoanRowMapper(), accountId);
    }

    @Override
    protected String getFindByIdSql(){
        return "SELECT * FROM loan WHERE id = ?";
    }

    @Override
    protected RowMapper<Loan> getRowMapper() {
        return new LoanRowMapper();
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO loan (account_id, principal_amount, remaining_balance, interest_rate," +
                " term_months, created_at, overdue, paid)" +
                " VALUES (?, ?, ?, ?, ? ,?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE loan SET account_id = ?, principal_amount = ?, remaining_balance = ?, " +
                "interest_rate = ?, term_months = ?, created_at = ?, overdue = ?, paid = ? WHERE id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE FROM loan WHERE id = ?";
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Loan loan, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
        if(sql.startsWith("DELETE")){
            ps.setLong(1, loan.getId());
        }
        ps.setLong(1, loan.getAccountId());
        ps.setBigDecimal(2, loan.getPrincipalAmount());
        ps.setBigDecimal(3, loan.getRemainingAmountToPay());
        ps.setBigDecimal(4, loan.getInterestRate());
        ps.setInt(5, loan.getTermMonths());
        ps.setTimestamp(6, Timestamp.valueOf(loan.getCreatedAt()));
        ps.setBoolean(7, loan.isOverdue());
        ps.setBoolean(8, loan.isPaid());
        if (sql.startsWith("UPDATE")) {
            ps.setLong(9, loan.getId());
        }
        return ps;
    }

}
