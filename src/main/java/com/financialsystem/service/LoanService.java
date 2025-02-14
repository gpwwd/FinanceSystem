package com.financialsystem.service;

import com.financialsystem.domain.Account;
import com.financialsystem.domain.Loan;
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

    @Autowired
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Long issueLoan(Long accountId, BigDecimal amount, BigDecimal interestRate, int termMonths) {
        Account account = EntityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Loan loan = Loan.create(accountId, amount, interestRate, termMonths);
        account.replenish(amount);
        accountRepository.update(account);
        return loanRepository.create(loan);
    }
}
