package com.financialsystem.service;

import com.financialsystem.domain.Account;
import com.financialsystem.domain.Deposit;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    public Long create(Long accountId, double annualInterestRate){
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Deposit deposit = Deposit.create(accountId, annualInterestRate);
        return depositRepository.create(deposit);
    }

    @Transactional
    public Deposit withdraw(Long id, BigDecimal amount){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        Long accountId = deposit.getAccountId();
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        deposit.withdraw(amount);
        account.replenish(amount);
        depositRepository.update(deposit);
        accountRepository.update(account);
        return deposit;
    }

    public Deposit replenish(Long id, BigDecimal amount){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        Long accountId = deposit.getAccountId();
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        deposit.replenish(amount);
        account.withdraw(amount);
        depositRepository.update(deposit);
        accountRepository.update(account);
        return deposit;
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount){
        Deposit fromDeposit = entityFinder.findEntityById(fromId, depositRepository, "Депозит");
        Deposit toDeposit = entityFinder.findEntityById(toId, depositRepository, "Депозит");

        fromDeposit.transfer(amount, toDeposit);

        depositRepository.update(fromDeposit);
        depositRepository.update(toDeposit);
    }

    // реализовать пополнение через scheduler

    public Long blockDeposit(Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.block();
        return depositRepository.update(deposit);
    }

    public Long unblockDeposit(Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.unblock();
        return depositRepository.update(deposit);
    }

    public Long freeDeposit(Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.freeze();
        return depositRepository.update(deposit);
    }

    public Long unfreeDeposit(Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.unfreeze();
        return depositRepository.update(deposit);
    }
}
