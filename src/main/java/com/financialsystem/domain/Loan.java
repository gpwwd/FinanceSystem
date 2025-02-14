package com.financialsystem.domain;

import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.LoanRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @ToString
@NoArgsConstructor
public class Loan {
    private Long id;
    private Long accountId;
    private BigDecimal principalAmount;
    private BigDecimal remainingBalance;
    private BigDecimal interestRate;
    private int termMonths;
    private LocalDateTime createdAt;
    private boolean overdue;

    public static Loan create(Long accountId, BigDecimal principalAmount, BigDecimal interestRate, int termMonths) {
        Loan loan = new Loan();
        loan.accountId = accountId;
        loan.principalAmount = principalAmount;
        loan.remainingBalance = calculateTotalPayable(principalAmount, interestRate);
        loan.interestRate = interestRate;
        loan.termMonths = termMonths;
        loan.createdAt = LocalDateTime.now();
        loan.overdue = false;
        return loan;
    }

    private static BigDecimal calculateTotalPayable(BigDecimal principal, BigDecimal interestRate) {
        BigDecimal floatPercents = interestRate.divide(BigDecimal.valueOf(100));
        BigDecimal interest = principal.multiply(floatPercents);
        return principal.add(interest);
    }

    public void makePayment(BigDecimal amount) {
        if (remainingBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Сумма платежа больше остатка кредита");
        }
        remainingBalance = remainingBalance.subtract(amount);
    }

    public boolean isPaidOff() {
        return remainingBalance.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean checkOverdue() {
        return LocalDateTime.now().isAfter(createdAt.plusMonths(termMonths));
    }

    public void applyOverdue(LoanRepository loanRepository, AccountRepository accountRepository) {
        if(checkOverdue()) {
            this.overdue = true;
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Аккаунт с" + accountId + "не найден"));
            account.block();
            loanRepository.update(this);
            accountRepository.update(account);
        }
    }

    public void makePayment(BigDecimal amount, LoanRepository loanRepository) {
        if (amount.compareTo(remainingBalance) >= 0) {
            remainingBalance = BigDecimal.ZERO;
        } else {
            remainingBalance = remainingBalance.subtract(amount);
        }

        loanRepository.update(this);
    }
}
