package com.financialsystem.service;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.Loan;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.LoanRepository;
import com.financialsystem.util.EntityFinder;
import com.financialsystem.util.LoanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        Loan loan = Loan.createCustomRateLoan(accountId, amount, termMonths, loanConfig);
        account.replenish(amount);
        accountRepository.update(account);
        return loanRepository.create(loan);
    }

    @Transactional
    public Long issueLoanWithFixedInterestRate(Long accountId, BigDecimal amount, int termMonths) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Loan loan = Loan.createFixedRateLoan(accountId, amount, termMonths);
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
        accountRepository.update(account);
        return loanRepository.update(loan);
    }

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void checkLoansOverdue() {
        List<Loan> loans = loanRepository.findAll();
        List<Loan> loansToUpdate = new ArrayList<>();

        for(var loan : loans ){
            if(loan.isGoneOverdue()){
                loan.applyOverduePenalty();
                loansToUpdate.add(loan);
                Long accountId = loan.getAccountId();
                Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
                account.block();
                accountRepository.update(account);
            }
        }

        loanRepository.batchUpdate(loansToUpdate);
    }
}
