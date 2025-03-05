package com.financialsystem.service;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.Deposit;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.status.DepositStatus;
import com.financialsystem.dto.response.DepositResponseDto;
import com.financialsystem.mapper.DepositMapper;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.repository.user.ClientRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public Long create(Long userId, Long accountId, int termMonths, BigDecimal principalBalance) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        account.verifyOwner(userId);
        account.withdraw(principalBalance);
        Deposit deposit = Deposit.create(accountId, termMonths, principalBalance);
        accountRepository.update(account);
        return depositRepository.create(deposit);
    }

    @Transactional
    @PreAuthorize("hasAuthority('CLIENT')")
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

    @Transactional
    @PreAuthorize("hasAuthority('CLIENT')")
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

    @Transactional
    @PreAuthorize("hasAuthority('CLIENT')")
    public void transfer(Long userId, Long fromId, Long toId, BigDecimal amount){
        Deposit fromDeposit = entityFinder.findEntityById(fromId, depositRepository, "Депозит");
        Deposit toDeposit = entityFinder.findEntityById(toId, depositRepository, "Депозит");

        Account fromDepositAccount = entityFinder.findEntityById(fromDeposit.getAccountId(), accountRepository, "Аккаунт");
        Account toDepositAccount = entityFinder.findEntityById(toDeposit.getAccountId(), accountRepository, "Аккаунт");
        fromDepositAccount.verifyOwner(userId);
        toDepositAccount.verifyOwner(userId);

        fromDeposit.transfer(amount, toDeposit);

        depositRepository.update(fromDeposit);
        depositRepository.update(toDeposit);
    }

    @Transactional
    @PreAuthorize("hasAuthority('CLIENT')")
    public BigDecimal retrieveMoney(Long userId, Long id){
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        Account account = entityFinder.findEntityById(deposit.getAccountId(), accountRepository, "Аккаунт");
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

    @PreAuthorize("hasAuthority('MANAGER')")
    public void blockDeposit(Long id){
        setStatus(id, DepositStatus.BLOCKED);
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    public void unblockDeposit(Long id){
        setStatus(id, DepositStatus.ACTIVE);
    }

    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public void freezeDeposit(BankingUserDetails userDetails, Long id){
        verifyFreezeDepositAccess(userDetails, id);
        setStatus(id, DepositStatus.FROZEN);
    }

    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public void unfreezeDeposit(BankingUserDetails userDetails, Long id){
        verifyFreezeDepositAccess(userDetails, id);
        setStatus(id, DepositStatus.ACTIVE);
    }

    private void verifyFreezeDepositAccess(BankingUserDetails userDetails, Long depositId) {
        if (userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("MANAGER"))) {
            return;
        }

        Deposit deposit = entityFinder.findEntityById(depositId, depositRepository, "Депозит");
        Account account = entityFinder.findEntityById(deposit.getAccountId(), accountRepository, "Аккаунт");

        account.verifyOwner(userDetails.getId());
    }

    private void setStatus(Long id, DepositStatus status) {
        Deposit deposit = entityFinder.findEntityById(id, depositRepository, "Депозит");
        deposit.setStatus(status);
        depositRepository.update(deposit);
    }

    @PreAuthorize("hasAuthority('CLIENT') or hasAuthority('MANAGER')")
    public List<DepositResponseDto> getDepositsForAccount(BankingUserDetails userDetails, Long accountId) {
        verifyUserAccessToAccount(userDetails, accountId);
        return depositRepository.findDepositsByAccountId(accountId).stream().map(DepositMapper::toDepositResponseDto)
                .collect(Collectors.toList());
    }

    private void verifyUserAccessToAccount(BankingUserDetails userDetails, Long accountId) {
        if (userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("MANAGER"))) {
            return;
        }

        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        if(!Objects.equals(account.getOwnerId(), userDetails.getId())) {
            throw new AccessDeniedException("Этот счет не принадлежит аутенфицированному клиенту");
        }
    }
}
