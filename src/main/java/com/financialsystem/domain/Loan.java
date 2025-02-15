package com.financialsystem.domain;

import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.LoanRepository;
import com.financialsystem.util.EntityFinder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @ToString
@NoArgsConstructor
public class Loan {
    private Long id;
    private Long accountId;
    private BigDecimal principalAmount;
    private BigDecimal remainingAmountToPay;
    private BigDecimal interestRate;
    private int termMonths;
    private LocalDateTime createdAt;
    private boolean overdue;

    public static Loan createWithCustomInterestRate(Long accountId, BigDecimal principalAmount, int termMonths,
                                                    LoanConfig loanConfig) {
        Loan loan = new Loan();
        loan.accountId = accountId;
        loan.principalAmount = principalAmount;
        BigDecimal interestRate = calculateCustomInterestRate(principalAmount, termMonths, loanConfig);
        loan.interestRate = interestRate;
        loan.remainingAmountToPay = calculateAmountToPayWithInterest(principalAmount, interestRate);
        loan.termMonths = termMonths;
        loan.createdAt = LocalDateTime.now();
        loan.overdue = false;
        return loan;
    }

    public static Loan createWithFixedInterestRate(Long accountId, BigDecimal principalAmount, LoanTerm loanTerm) {
        Loan loan = new Loan();
        loan.accountId = accountId;
        loan.principalAmount = principalAmount;
        loan.remainingAmountToPay = calculateAmountToPayWithInterest(principalAmount, loanTerm.getInterestRate());
        loan.interestRate = loanTerm.getInterestRate();
        loan.termMonths = loanTerm.getTermMonths();
        loan.createdAt = LocalDateTime.now();
        loan.overdue = false;
        return loan;
    }

    private static BigDecimal calculateCustomInterestRate(BigDecimal principalAmount, int termMonths, LoanConfig loanConfig){
        BigDecimal baseRate = loanConfig.getBaseRate();

        if (principalAmount.compareTo(loanConfig.getLargeLoanThreshold()) > 0) {
            baseRate = baseRate.subtract(loanConfig.getLargeLoanDiscount());
        } else if (principalAmount.compareTo(loanConfig.getSmallLoanThreshold()) < 0) {
            baseRate = baseRate.add(loanConfig.getSmallLoanPenalty());
        }

        if (termMonths > loanConfig.getLongTermThreshold()) {
            baseRate = baseRate.add(loanConfig.getLongTermPenalty());
        } else if (termMonths <= loanConfig.getShortTermThreshold()) {
            baseRate = baseRate.subtract(loanConfig.getShortTermDiscount());
        }

        return baseRate;
    }

    private static BigDecimal calculateAmountToPayWithInterest(BigDecimal principal, BigDecimal interestRate) {
        BigDecimal floatPercents = interestRate.divide(BigDecimal.valueOf(100));
        BigDecimal interest = principal.multiply(floatPercents);
        return principal.add(interest);
    }

    public void makePayment(BigDecimal amount) {
        if (remainingAmountToPay.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Сумма платежа больше остатка кредита");
        }
        remainingAmountToPay = remainingAmountToPay.subtract(amount);
    }

    public boolean isPaidOff() {
        return remainingAmountToPay.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean checkOverdue() {
        return LocalDateTime.now().isAfter(createdAt.plusMonths(termMonths));
    }

    public void applyOverdue(LoanRepository loanRepository, AccountRepository accountRepository) {
        if(checkOverdue()) {
            this.overdue = true;
            Account account = EntityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
            account.block();
            loanRepository.update(this);
            accountRepository.update(account);
        }
    }

    public void makePayment(BigDecimal amount, LoanRepository loanRepository) {
        if (amount.compareTo(remainingAmountToPay) >= 0) {
            remainingAmountToPay = BigDecimal.ZERO;
        } else {
            remainingAmountToPay = remainingAmountToPay.subtract(amount);
        }

        loanRepository.update(this);
    }
}
