package com.financialsystem.domain.model.account;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.FinancialEntity;
import com.financialsystem.domain.status.AccountStatus;
import com.financialsystem.dto.database.account.AccountDatabaseDto;
import com.financialsystem.dto.response.AccountResponseDto;
import lombok.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account implements FinancialEntity {
    @Getter
    protected Long id;
    @Setter(AccessLevel.PRIVATE)
    protected AccountStatus status;
    @Getter
    protected Long ownerId;
    @Getter
    protected Long enterpriseId;
    protected Long bankId;
    protected Currency currency;
    protected LocalDateTime createdAt;
    protected BigDecimal balance;

    public AccountDatabaseDto toDto() {
        return new AccountDatabaseDto(
                id, status, ownerId, enterpriseId, bankId, currency, createdAt, balance);
    }

    public void block() {
        setStatus(AccountStatus.BLOCKED);
    }

    @Transactional
    public void withdraw(BigDecimal amount) {
        checkAccountState();
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств");
        }
        this.balance = balance.subtract(amount);
    }

    @Transactional
    public void replenish(BigDecimal amount) {
        checkAccountState();
        this.balance = balance.add(amount);
    }

    private void checkAccountState() {
        if(status == AccountStatus.BLOCKED || status == AccountStatus.FROZEN) {
            throw new IllegalStateException("Account status must be ACTIVE, actual status is: " + this.status);
        }
    }

    public void verifyOwner(Long userId) {
        if (!ownerId.equals(userId)) {
            throw new AccessDeniedException("Вы не можете управлять этим счетом!");
        }
    }

    public void closeAccountCheck() {
        checkAccountState();
        if(this.balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("You can not close account with positive balance!");
        }
    }

    public AccountResponseDto toAccountResponseDto() {
        return new AccountResponseDto(
                id,
                status,
                ownerId,
                enterpriseId,
                bankId,
                currency,
                createdAt,
                balance
        );
    }
}
