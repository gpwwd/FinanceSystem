package com.financialsystem.service;

import com.financialsystem.domain.Account;
import com.financialsystem.domain.Deposit;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.util.EntityFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
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

    public Long create(Long accountId, BigDecimal annualInterestRate, int termMonths, BigDecimal principalBalance) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        Deposit deposit = Deposit.create(accountId, annualInterestRate, termMonths, principalBalance);
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

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void processMonthlyInterest() {
        List<Deposit> deposits = depositRepository.findAll();

        for (Deposit deposit : deposits) {

            log.info("Проверка депозита {}. Дата создания: {}, Дата последнего начисления: {}, баланс: {}",
                    deposit.getId(), deposit.getCreatedAt(), deposit.getLastInterestDate(), deposit.getBalance());
            log.info("Сравнение: {} vs {}", LocalDateTime.now(), deposit.getCreatedAt().plusMinutes(deposit.getTermMonths()));

            if (!deposit.isActive()) continue;

            if (deposit.isGoneOverdue()) {
                deposit.addMonthlyInterest();
                log.info("Закрытие депозита: {}", deposit);
                deposit.setBlocked(true);
                depositRepository.update(deposit);
            } else if(deposit.isMonthPassed()) {
                deposit.addMonthlyInterest();
                depositRepository.update(deposit);
            }
        }
    }

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

    public Long freezeDeposit(Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.freeze();
        return depositRepository.update(deposit);
    }

    public Long unfreezeDeposit(Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.unfreeze();
        return depositRepository.update(deposit);
    }
}
