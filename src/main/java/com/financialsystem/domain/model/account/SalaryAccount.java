package com.financialsystem.domain.model.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.domain.status.PendingEntityStatus;
import com.financialsystem.domain.status.SalaryProjectStatus;
import com.financialsystem.dto.database.account.AccountDatabaseDto;
import com.financialsystem.dto.database.account.SalaryAccountDatabaseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalaryAccount extends Account {

    @Getter
    private Long salaryProjectId;
    private PendingEntityStatus salaryAccountStatus;

    public static SalaryAccount create(Long ownerId, Long bankId, Currency currency, Long salaryProjectId) {
        SalaryAccount salaryAccount = new SalaryAccount();
        salaryAccount.ownerId = ownerId;
        salaryAccount.bankId = bankId;
        salaryAccount.status = AccountStatus.ACTIVE;
        salaryAccount.balance = BigDecimal.ZERO;
        salaryAccount.currency = currency;
        salaryAccount.createdAt = LocalDateTime.now();
        salaryAccount.salaryProjectId = salaryProjectId;
        salaryAccount.salaryAccountStatus = PendingEntityStatus.PENDING;
        return salaryAccount;
    }

    public static SalaryAccount fromDto(SalaryAccountDatabaseDto dto) {
        SalaryAccount salaryAccount = new SalaryAccount();
        salaryAccount.id = dto.getId();
        salaryAccount.ownerId = dto.getOwnerId();
        salaryAccount.bankId = dto.getBankId();
        salaryAccount.status = dto.getStatus();
        salaryAccount.balance = dto.getBalance();
        salaryAccount.currency = dto.getCurrency();
        salaryAccount.createdAt = dto.getCreatedAt();
        salaryAccount.salaryProjectId = dto.getSalaryProjectId();
        salaryAccount.salaryAccountStatus = dto.getSalaryAccountStatus();
        return salaryAccount;
    }

    public SalaryAccountDatabaseDto toDto() {
        return new SalaryAccountDatabaseDto(
                id, status, ownerId, bankId, currency, createdAt, balance, salaryProjectId, salaryAccountStatus);
    }

    public void approveSalaryAccountStatus() {
        this.salaryAccountStatus = PendingEntityStatus.APPROVED;
        this.createdAt = LocalDateTime.now();
    }
}
