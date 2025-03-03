package com.financialsystem.service;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.Deposit;
import com.financialsystem.domain.status.DepositStatus;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DepositService {

    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;
    private final EntityFinder entityFinder;

    @Autowired
    public DepositService(DepositRepository depositRepository, AccountRepository accountRepository, EntityFinder entityFinder) {
        this.depositRepository = depositRepository;
        this.accountRepository = accountRepository;
        this.entityFinder = entityFinder;
    }

    @PreAuthorize("hasAuthority('CLIENT')")
    public Long create(Long userId, Long accountId, BigDecimal annualInterestRate, int termMonths, BigDecimal principalBalance) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        account.verifyOwner(userId);
        account.withdraw(principalBalance);
        Deposit deposit = Deposit.create(accountId, annualInterestRate, termMonths, principalBalance);
        accountRepository.update(account);
        return depositRepository.create(deposit);
    }

    @Transactional
    public Deposit withdrawInterest(Long userId, Long id, BigDecimal amount){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        Long accountId = deposit.getAccountId();
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        account.verifyOwner(userId);
        deposit.withdrawInterest(amount);
        account.replenish(amount);
        depositRepository.update(deposit);
        accountRepository.update(account);
        return deposit;
    }

    public Deposit replenish(Long userId, Long id, BigDecimal amount){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        Long accountId = deposit.getAccountId();
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        account.verifyOwner(userId);
        deposit.replenish(amount);
        account.withdraw(amount);
        depositRepository.update(deposit);
        accountRepository.update(account);
        return deposit;
    }

    public void transfer(Long userId, Long fromId, Long toId, BigDecimal amount){
        Deposit fromDeposit = entityFinder.findEntityById(fromId, depositRepository, "Депозит");
        Deposit toDeposit = entityFinder.findEntityById(toId, depositRepository, "Депозит");

        Long fromDepositAccountId = fromDeposit.getAccountId();
        Long toDepositAccountId = toDeposit.getAccountId();
        Account fromDepositAccount = entityFinder.findEntityById(fromDepositAccountId, accountRepository, "Аккаунт");
        Account toDepositAccount = entityFinder.findEntityById(toDepositAccountId, accountRepository, "Аккаунт");
        fromDepositAccount.verifyOwner(userId);
        toDepositAccount.verifyOwner(userId);

        fromDeposit.transfer(amount, toDeposit);

        depositRepository.update(fromDeposit);
        depositRepository.update(toDeposit);
    }

    @Transactional
    public BigDecimal retrieveMoney(Long userId, Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        Long accountId = deposit.getAccountId();
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        account.verifyOwner(userId);
        BigDecimal retrievedMoney = deposit.retrieveMoney();
        account.replenish(retrievedMoney);
        depositRepository.update(deposit);
        accountRepository.update(account);
        return retrievedMoney;
    }

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void processMonthlyInterest() {
        List<Deposit> deposits = depositRepository.findAll();
        List<Deposit> depositsToUpdate = new ArrayList<>();

        for (Deposit deposit : deposits) {
            if (deposit.addMonthlyInterestIfRequired()) {
                depositsToUpdate.add(deposit);
            }
        }

        depositRepository.batchUpdate(depositsToUpdate);
    }

    public void blockDeposit(Long id){
        setStatus(id, DepositStatus.BLOCKED);
    }

    public void unblockDeposit(Long id){
        setStatus(id, DepositStatus.ACTIVE);
    }

    public void freezeDeposit(Long id){
        setStatus(id, DepositStatus.FROZEN);
    }

    public void unfreezeDeposit(Long id){
        setStatus(id, DepositStatus.ACTIVE);
    }

    private void setStatus(Long id, DepositStatus status) {
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.setStatus(status);
        depositRepository.update(deposit);
    }
}
