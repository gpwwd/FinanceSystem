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
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanConfig loanConfig;

    @Autowired
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository,
                       LoanConfig loanConfig) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.loanConfig = loanConfig;
    }

    @Transactional
    public Long issueLoanWithCustomInterestRate(Long accountId, BigDecimal amount, int termMonths) {
        Account account = EntityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Loan loan = Loan.createWithCustomInterestRate(accountId, amount, termMonths, loanConfig);
        account.replenish(amount);
        accountRepository.update(account);
        return loanRepository.create(loan);
    }

    @Transactional
    public Long issueLoanWithFixedInterestRate(Long accountId, BigDecimal amount, String loanTerm) {
        Account account = EntityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Loan loan = Loan.createWithFixedInterestRate(accountId, amount, LoanTerm.valueOf(loanTerm.toUpperCase()));
        account.replenish(amount);
        accountRepository.update(account);
        return loanRepository.create(loan);
    }
}
