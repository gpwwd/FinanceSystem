package com.financialsystem.domain.model.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.domain.status.PendingEntityStatus;
import com.financialsystem.domain.status.SalaryProjectStatus;
import com.financialsystem.dto.database.account.AccountDatabaseDto;
import com.financialsystem.dto.database.account.SalaryAccountDatabaseDto;
import com.financialsystem.dto.response.AccountReposonseDto;
import com.financialsystem.dto.response.SalaryAccountResponseDto;
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
    @Getter
    private BigDecimal salaryAmount;

    public static SalaryAccount create(Long ownerId, Long bankId, Currency currency, Long salaryProjectId, BigDecimal salaryAmount) {
        SalaryAccount salaryAccount = new SalaryAccount();
        salaryAccount.ownerId = ownerId;
        salaryAccount.bankId = bankId;
        salaryAccount.status = AccountStatus.ACTIVE;
        salaryAccount.balance = BigDecimal.ZERO;
        salaryAccount.currency = currency;
        salaryAccount.createdAt = LocalDateTime.now();
        salaryAccount.salaryProjectId = salaryProjectId;
        salaryAccount.salaryAccountStatus = PendingEntityStatus.PENDING;
        salaryAccount.salaryAmount = salaryAmount;
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
        salaryAccount.salaryAmount = dto.getSalaryAmount();
        return salaryAccount;
    }

    public SalaryAccountDatabaseDto toDto() {
        return new SalaryAccountDatabaseDto(
                id, status, ownerId, enterpriseId, bankId, currency, createdAt, balance, salaryProjectId, salaryAccountStatus, salaryAmount);
    }

    public void approveSalaryAccountStatus() {
        this.salaryAccountStatus = PendingEntityStatus.APPROVED;
        this.createdAt = LocalDateTime.now();
    }

    public SalaryAccountResponseDto toSalaryAccountResponseDto() {
        return new SalaryAccountResponseDto(
                id, status, ownerId, enterpriseId, bankId, currency,
                createdAt, balance, salaryProjectId, salaryAccountStatus, salaryAmount
        );
    }

    public boolean isStatus(PendingEntityStatus salaryAccountStatus) {
        return this.salaryAccountStatus.equals(salaryAccountStatus);
    }
}
