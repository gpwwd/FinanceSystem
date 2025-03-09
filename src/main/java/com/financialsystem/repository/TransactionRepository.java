package com.financialsystem.repository;

import com.financialsystem.domain.model.transaction.Transaction;
import com.financialsystem.dto.database.TransactionDatabaseDto;
import com.financialsystem.rowMapper.TransactionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TransactionRepository extends GenericRepository<Transaction, TransactionDatabaseDto> {

    @Autowired
    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO transaction (from_entity_id, from_type, to_entity_id, to_type, " +
                "amount, timestamp) "  +
                "VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE transaction SET from_entity_id = ?, from_type = ?, to_entity_id = ?, to_type = ?, " +
                "amount = ?, timestamp = ? WHERE id = ?";
    }

    @Override
    protected String getFindByIdSql() {
        return "select * from transaction where id = ?";
    }

    @Override
    protected String getDeleteSql() {
        return "DELETE * from transaction where id = ?";
    }

    @Override
    protected String getFindAllSql() {
        return "select * from transaction";
    }

    @Override
    protected RowMapper<TransactionDatabaseDto> getRowMapper() {
        return new TransactionRowMapper();
    }

    @Override
    protected PreparedStatement createPreparedStatement(String sql, Transaction transaction, Connection connection) throws SQLException {
        TransactionDatabaseDto transactionDto = transaction.toDto();
        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

        if (sql.startsWith("DELETE")) {
            ps.setLong(1, transactionDto.getId());
            return ps;
        }

        ps.setLong(1, transactionDto.getFromEntityId());
        ps.setString(2, transactionDto.getFromType().name());
        ps.setLong(3, transactionDto.getToEntityId());
        ps.setString(4, transactionDto.getToType().name());
        ps.setBigDecimal(5, transactionDto.getAmount());

        if (sql.startsWith("INSERT")) {
            ps.setTimestamp(6, Timestamp.valueOf(transactionDto.getTimestamp()));
        } else if (sql.startsWith("UPDATE")) {
            ps.setTimestamp(6, getExistingCreatedAt(transactionDto.getId()));
        }

        if (sql.startsWith("UPDATE")) {
            ps.setLong(7, transactionDto.getId());
        }

        return ps;
    }

    public void batchUpdate(List<Transaction> transactions) {
        String sql = getCreateSql();
        List<TransactionDatabaseDto> transactionDatabaseDtos = transactions.stream().map(Transaction::toDto).toList();
        jdbcTemplate.batchUpdate(sql, transactionDatabaseDtos, transactionDatabaseDtos.size(),
                (ps, transactionDto) -> {
                    ps.setLong(1, transactionDto.getFromEntityId());
                    ps.setString(2, transactionDto.getFromType().name());
                    ps.setLong(3, transactionDto.getToEntityId());
                    ps.setString(4, transactionDto.getToType().name());
                    ps.setBigDecimal(5, transactionDto.getAmount());
                    ps.setTimestamp(6, Timestamp.valueOf(transactionDto.getTimestamp()));
                }
        );
    }

    private Timestamp getExistingCreatedAt(Long transactionId) {
        String sql = "SELECT timestamp FROM transaction WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Timestamp.class, transactionId);
    }

    @Override
    protected Transaction fromDto(TransactionDatabaseDto transaction) {
        return null;
    }
}
