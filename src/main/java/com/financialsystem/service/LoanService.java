package com.financialsystem.service;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.Loan;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.status.PendingEntityStatus;
import com.financialsystem.domain.strategy.CustomInterestStrategy;
import com.financialsystem.domain.strategy.FixedInterestStrategy;
import com.financialsystem.domain.strategy.InterestCalculationStrategy;
import com.financialsystem.dto.database.PendingLoanDatabaseDto;
import com.financialsystem.dto.response.PendingLoanResponseDto;
import com.financialsystem.mapper.LoanMapper;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.loan.LoanRepository;
import com.financialsystem.repository.loan.PendingLoanRepository;
import com.financialsystem.util.EntityFinder;
import com.financialsystem.util.LoanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final PendingLoanRepository pendingLoanRepository;
    private final AccountRepository accountRepository;
    private final LoanConfig loanConfig;
    private final EntityFinder entityFinder;

    @Autowired
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository,
                       LoanConfig loanConfig, EntityFinder entityFinder, PendingLoanRepository pendingLoanRepository) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.loanConfig = loanConfig;
        this.entityFinder = entityFinder;
        this.pendingLoanRepository = pendingLoanRepository;
    }

    @Transactional
    public Long createLoanRequest(BankingUserDetails userDetails,
                                  Long accountId, BigDecimal amount,
                                  int termMonths, boolean isFixedInterest){
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        account.verifyOwner(userDetails.getId());
        FixedInterestStrategy.validateLoanTerm(termMonths, isFixedInterest);
        return pendingLoanRepository.create(new PendingLoanDatabaseDto(accountId, amount, termMonths, isFixedInterest));
    }

    public List<PendingLoanResponseDto> getPendingLoans() {
        return pendingLoanRepository.findAll().stream().map(LoanMapper::toPendingLoanResponseDto)
                .collect(Collectors.toList());
    }

    public List<PendingLoanResponseDto> getPendingLoansForUserAccount(BankingUserDetails userDetails, Long userAccountId) {
        Account account = entityFinder.findEntityById(userAccountId, accountRepository, "Аккаунт");
        account.verifyOwner(userDetails.getId());
        return pendingLoanRepository.findByAccountId(userAccountId).stream().map(LoanMapper::toPendingLoanResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long approveLoan(Long pendingLoanId){
        PendingLoanDatabaseDto pendingLoanDatabaseDto = entityFinder.findEntityById(pendingLoanId, pendingLoanRepository, "Заявка на кредит");
        pendingLoanRepository.delete(pendingLoanDatabaseDto);
        if(pendingLoanDatabaseDto.isFixedInterest()){
            return issueLoan(pendingLoanDatabaseDto.getAccountId(),
                    pendingLoanDatabaseDto.getPrincipalAmount(), pendingLoanDatabaseDto.getTermMonths(),
                    new FixedInterestStrategy());
        } else {
            return issueLoan(pendingLoanDatabaseDto.getAccountId(),
                    pendingLoanDatabaseDto.getPrincipalAmount(), pendingLoanDatabaseDto.getTermMonths(),
                    new CustomInterestStrategy(loanConfig));
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('MANAGER')")
    public Long rejectLoan(Long pendingLoanId){
        PendingLoanDatabaseDto pendingLoanDatabaseDto = entityFinder.findEntityById(pendingLoanId, pendingLoanRepository, "Заявка на кредит");
        pendingLoanDatabaseDto.setRequestStatus(PendingEntityStatus.REJECTED);
        return pendingLoanRepository.update(pendingLoanDatabaseDto);
    }

    @Transactional
    protected Long issueLoan(Long accountId, BigDecimal amount, int termMonths, InterestCalculationStrategy strategy) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Loan loan = Loan.create(accountId, amount, termMonths, loanConfig, strategy);
        account.replenish(amount);
        accountRepository.update(account);
        return loanRepository.create(loan);
    }

    @Transactional
    public Long makePayment(BankingUserDetails userDetails, BigDecimal amount, Long loanId) {
        Loan loan = entityFinder.findEntityById(loanId, loanRepository, "Кредит");
        Long loanAccountId = loan.getAccountId();
        Account account = entityFinder.findEntityById(loanAccountId, accountRepository, "Аккаунт");
        account.verifyOwner(userDetails.getId());
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
