package com.financialsystem.service;

import com.financialsystem.domain.Account;
import com.financialsystem.domain.Loan;
import com.financialsystem.domain.LoanConfig;
import com.financialsystem.domain.LoanTerm;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.LoanRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanConfig loanConfig;
    private final EntityFinder entityFinder;

    @Autowired
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository,
                       LoanConfig loanConfig, EntityFinder entityFinder) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.loanConfig = loanConfig;
        this.entityFinder = entityFinder;
    }

    @Transactional
    public Long issueLoanWithCustomInterestRate(Long accountId, BigDecimal amount, int termMonths) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Loan loan = Loan.createWithCustomInterestRate(accountId, amount, termMonths, loanConfig);
        account.replenish(amount);
        accountRepository.update(account);
        return loanRepository.create(loan);
    }

    @Transactional
    public Long issueLoanWithFixedInterestRate(Long accountId, BigDecimal amount, String loanTerm) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Loan loan = Loan.createWithFixedInterestRate(accountId, amount, LoanTerm.valueOf(loanTerm.toUpperCase()));
        account.replenish(amount);
        accountRepository.update(account);
        return loanRepository.create(loan);
    }

    @Transactional
    public Long makePayment(BigDecimal amount, Long loanId) {
        Loan loan = entityFinder.findEntityById(loanId, loanRepository, "Кредит");
        Long loanAccountId = loan.getAccountId();
        Account account = entityFinder.findEntityById(loanAccountId, accountRepository, "Аккаунт");
        account.withdraw(amount);
        loan.makePayment(amount);
        if(loan.isPaidOff()){
            accountRepository.update(account);
            return handlePaidOffLoan(loan);
        }
        accountRepository.update(account);
        return loanRepository.update(loan);
    }

    private Long handlePaidOffLoan(Loan loan) {
        loan.markAsPaidOff();
        return loanRepository.update(loan);
    }
}
