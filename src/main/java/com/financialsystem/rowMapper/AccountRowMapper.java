package com.financialsystem.rowMapper;

import com.financialsystem.domain.model.account.Account;
import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long enterpriseId = rs.getLong("enterprise_id");
        if (rs.wasNull()) {
            enterpriseId = null;
        }

        Long ownerId = rs.getLong("owner_id");
        if (rs.wasNull()) {
            ownerId = null;
        }


        return new Account(
                rs.getLong("id"),
                AccountStatus.valueOf(rs.getString("status")),
                ownerId,
                enterpriseId,
                rs.getLong("bank_id"),
                Currency.valueOf(rs.getString("currency")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBigDecimal("balance")
        );
    }
}