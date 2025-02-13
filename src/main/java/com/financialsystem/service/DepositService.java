package com.financialsystem.service;

import com.financialsystem.domain.Deposit;
import com.financialsystem.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DepositService {

    private final DepositRepository depositRepository;

    @Autowired
    public DepositService(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    public Long create(String accountNumber, double annualInterestRate){
        Deposit deposit = Deposit.create(accountNumber, annualInterestRate);
        return depositRepository.create(deposit);
    }

    public Deposit withdraw(Long id, BigDecimal amount){
        Deposit deposit = depositRepository.findById(id);
        deposit.withdraw(amount);
        depositRepository.update(deposit);
        return deposit;
    }

    public Deposit replenish(Long id, BigDecimal amount){
        Deposit deposit = depositRepository.findById(id);
        deposit.replenish(amount);
        depositRepository.update(deposit);
        return deposit;
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount){
        Deposit fromDeposit = depositRepository.findById(fromId);
        Deposit toDeposit = depositRepository.findById(toId);

        fromDeposit.transfer(amount, toDeposit);

        depositRepository.update(fromDeposit);
        depositRepository.update(toDeposit);
    }

    // реализовать пополнение через scheduler

    public Long blockDeposit(Long id){
        Deposit deposit = depositRepository.findById(id);
        deposit.block();
        return depositRepository.update(deposit);
    }

    public Long unblockDeposit(Long id){
        Deposit deposit = depositRepository.findById(id);
        deposit.unblock();
        return depositRepository.update(deposit);
    }

    public Long freeDeposit(Long id){
        Deposit deposit = depositRepository.findById(id);
        deposit.freeze();
        return depositRepository.update(deposit);
    }

    public Long unfreeDeposit(Long id){
        Deposit deposit = depositRepository.findById(id);
        deposit.unfreeze();
        return depositRepository.update(deposit);
    }
}
