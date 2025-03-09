package com.financialsystem.dto.database.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=true)
public class SalaryAccountDatabaseDto extends AccountDatabaseDto {

    private Long salaryProjectId;

    public SalaryAccountDatabaseDto(Long id, AccountStatus status, Long ownerId, Long bankId, Currency currency,
                                    LocalDateTime createdAt, BigDecimal balance, Long salaryProjectId) {
        super(id, status, ownerId, bankId, currency, createdAt, balance);
        this.salaryProjectId = salaryProjectId;
    }
}
