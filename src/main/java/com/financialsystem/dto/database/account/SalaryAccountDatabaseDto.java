package com.financialsystem.dto.database.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.domain.status.PendingEntityStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=true)
public class SalaryAccountDatabaseDto extends AccountDatabaseDto {

    private Long salaryProjectId;
    private PendingEntityStatus salaryAccountStatus;
    private BigDecimal salaryAmount;

    public SalaryAccountDatabaseDto(Long id, AccountStatus status, Long ownerId, Long enterpriseId, Long bankId, Currency currency,
                                    LocalDateTime createdAt, BigDecimal balance, Long salaryProjectId, PendingEntityStatus salaryAccountStatus,
                                    BigDecimal salaryAmount) {
        super(id, status, ownerId, enterpriseId, bankId, currency, createdAt, balance);
        this.salaryProjectId = salaryProjectId;
        this.salaryAccountStatus = salaryAccountStatus;
        this.salaryAmount = salaryAmount;
    }
}
