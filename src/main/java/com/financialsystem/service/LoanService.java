package com.financialsystem.service;

import com.financialsystem.domain.model.account.Account;
import com.financialsystem.domain.model.Loan;
import com.financialsystem.domain.model.transaction.Transaction;
import com.financialsystem.domain.model.transaction.TransactionType;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.status.PendingEntityStatus;
import com.financialsystem.domain.strategy.CustomInterestStrategy;
import com.financialsystem.domain.strategy.FixedInterestStrategy;
import com.financialsystem.domain.strategy.InterestCalculationStrategy;
import com.financialsystem.dto.database.loan.PendingLoanDatabaseDto;
import com.financialsystem.dto.response.LoanResponseDto;
import com.financialsystem.dto.response.PendingLoanResponseDto;
import com.financialsystem.mapper.LoanMapper;
import com.financialsystem.repository.account.AccountRepository;
import com.financialsystem.repository.TransactionRepository;
import com.financialsystem.repository.loan.LoanRepository;
import com.financialsystem.repository.loan.PendingLoanRepository;
import com.financialsystem.util.EntityFinder;
import com.financialsystem.util.LoanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final PendingLoanRepository pendingLoanRepository;
    private final AccountRepository accountRepository;
    private final LoanConfig loanConfig;
    private final EntityFinder entityFinder;
    private final TransactionRepository transactionRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository,
                       LoanConfig loanConfig, EntityFinder entityFinder, PendingLoanRepository pendingLoanRepository,
                       TransactionRepository transactionRepository) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.loanConfig = loanConfig;
        this.entityFinder = entityFinder;
        this.pendingLoanRepository = pendingLoanRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Long createLoanRequest(BankingUserDetails userDetails,
                                  Long accountId, BigDecimal amount,
                                  int termMonths, boolean isFixedInterest){
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Счет");
        account.verifyOwner(userDetails.getId());
        FixedInterestStrategy.validateLoanTerm(termMonths, isFixedInterest);

        return pendingLoanRepository.create(new PendingLoanDatabaseDto(accountId, amount, termMonths, isFixedInterest));
    }

    public List<PendingLoanResponseDto> getPendingLoans() {
        return pendingLoanRepository.findAll().stream().map(LoanMapper::toPendingLoanResponseDto)
                .collect(Collectors.toList());
    }

    public List<LoanResponseDto> getLoans() {
        return loanRepository.findAll().stream().map(Loan::toLoanResponseDto)
                .collect(Collectors.toList());
    }

    public List<PendingLoanResponseDto> getPendingLoansForUserAccount(BankingUserDetails userDetails, Long userAccountId) {
        Account account = entityFinder.findEntityById(userAccountId, accountRepository, "Счет");
        account.verifyOwner(userDetails.getId());
        return pendingLoanRepository.findByAccountId(userAccountId).stream().map(LoanMapper::toPendingLoanResponseDto)
                .collect(Collectors.toList());
    }

    public List<LoanResponseDto> getApprovedLoansForUserAccount(BankingUserDetails userDetails, Long accountId) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Счет");
        account.verifyOwner(userDetails.getId());
        return loanRepository.findByAccountId(accountId).stream().map(Loan::toLoanResponseDto)
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
    public Long rejectLoan(Long pendingLoanId){
        PendingLoanDatabaseDto pendingLoanDatabaseDto = entityFinder.findEntityById(pendingLoanId, pendingLoanRepository, "Заявка на кредит");
        pendingLoanDatabaseDto.setRequestStatus(PendingEntityStatus.REJECTED);
        return pendingLoanRepository.update(pendingLoanDatabaseDto);
    }

    @Transactional
    protected Long issueLoan(Long accountId, BigDecimal amount, int termMonths, InterestCalculationStrategy strategy) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Счет");

        Loan loan = Loan.create(accountId, amount, termMonths, loanConfig, strategy);
        account.replenish(amount);

        Long createdLoanId = loanRepository.create(loan);
        accountRepository.update(account);

        Transaction transaction = Transaction.create(createdLoanId, TransactionType.LOAN, accountId, TransactionType.ACCOUNT, amount);
        transactionRepository.create(transaction);
        return createdLoanId;
    }

    @Transactional
    public Long makePayment(BankingUserDetails userDetails, BigDecimal amount, Long loanId) {
        Loan loan = entityFinder.findEntityById(loanId, loanRepository, "Кредит");
        Account account = entityFinder.findEntityById(loan.getAccountId(), accountRepository, "Счет");

        account.verifyOwner(userDetails.getId());
        account.withdraw(amount);
        loan.makePayment(amount);
        Transaction transaction = Transaction.create(account.getId(), TransactionType.ACCOUNT, loanId, TransactionType.LOAN, amount);

        transactionRepository.create(transaction);
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
