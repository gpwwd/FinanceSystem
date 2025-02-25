package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.Deposit;
import com.financialsystem.domain.status.DepositStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositRowMapper implements RowMapper<Deposit> {
    @Override
    public Deposit mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Deposit(
                rs.getLong("id"),
                rs.getBigDecimal("principal_balance"),
                rs.getBigDecimal("balance"),
                rs.getLong("account_id"),
                DepositStatus.valueOf(rs.getString("deposit_status")),
                rs.getBigDecimal("interest_rate"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("last_interest_date").toLocalDateTime(),
                rs.getInt("term_months")
        );
    }
}
