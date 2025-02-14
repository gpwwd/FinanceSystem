package com.financialsystem.service;

import com.financialsystem.domain.Account;
import com.financialsystem.domain.Deposit;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.repository.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DepositService {

    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public DepositService(DepositRepository depositRepository, AccountRepository accountRepository) {
        this.depositRepository = depositRepository;
        this.accountRepository = accountRepository;
    }

    public Long create(Long accountId, double annualInterestRate){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Аккаунт с " + accountId + " не найден"));
        Deposit deposit = Deposit.create(accountId, annualInterestRate);
        return depositRepository.create(deposit);
    }

    @Transactional
    public Deposit withdraw(Long id, BigDecimal amount){
        Deposit deposit = depositRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Депозит с " + id + " не найден"));
        Long accountId = deposit.getAccountId();
        Account account = accountRepository.findById(accountId).
                orElseThrow(() -> new RuntimeException("Аккаунт с " + id + " не найден"));
        deposit.withdraw(amount);
        account.replenish(amount);
        depositRepository.update(deposit);
        accountRepository.update(account);
        return deposit;
    }

    public Deposit replenish(Long id, BigDecimal amount){
        Deposit deposit = depositRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Депозит с " + id + " не найден"));
        Long accountId = deposit.getAccountId();
        Account account = accountRepository.findById(accountId).
                orElseThrow(() -> new RuntimeException("Аккаунт с " + id + " не найден"));
        deposit.replenish(amount);
        account.withdraw(amount);
        depositRepository.update(deposit);
        accountRepository.update(account);
        return deposit;
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount){
        Deposit fromDeposit = depositRepository.findById(fromId).
                orElseThrow(() -> new RuntimeException("Депозит с " + fromId + " не найден"));
        Deposit toDeposit = depositRepository.findById(toId).
                orElseThrow(() -> new RuntimeException("Депозит с " + toId + " не найден"));

        fromDeposit.transfer(amount, toDeposit);

        depositRepository.update(fromDeposit);
        depositRepository.update(toDeposit);
    }

    // реализовать пополнение через scheduler

    public Long blockDeposit(Long id){
        Deposit deposit = depositRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Депозит с " + id + " не найден"));
        deposit.block();
        return depositRepository.update(deposit);
    }

    public Long unblockDeposit(Long id){
        Deposit deposit = depositRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Депозит с " + id + " не найден"));
        deposit.unblock();
        return depositRepository.update(deposit);
    }

    public Long freeDeposit(Long id){
        Deposit deposit = depositRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Депозит с " + id + " не найден"));
        deposit.freeze();
        return depositRepository.update(deposit);
    }

    public Long unfreeDeposit(Long id){
        Deposit deposit = depositRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Депозит с " + id + " не найден"));
        deposit.unfreeze();
        return depositRepository.update(deposit);
    }
}
