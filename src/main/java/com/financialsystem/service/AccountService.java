package com.financialsystem.service;

import com.financialsystem.domain.model.account.Account;
import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.account.PersonalAccount;
import com.financialsystem.domain.model.deposit.Deposit;
import com.financialsystem.repository.account.AccountRepository;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.repository.user.ClientRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final DepositRepository depositRepository;
    private final EntityFinder entityFinder;

    @Autowired
    public AccountService(AccountRepository accountRepository, EntityFinder entityFinder,
                          ClientRepository clientRepository, DepositRepository depositRepository) {
        this.accountRepository = accountRepository;
        this.entityFinder = entityFinder;
        this.clientRepository = clientRepository;
        this.depositRepository = depositRepository;
    }

    @Transactional
    public Long createPersonalAccount(Long clientId, Long bankId, Currency currency) {
        entityFinder.findEntityById(clientId, clientRepository, "Клиент");
        Account account = PersonalAccount.create(clientId, bankId, currency);
        return accountRepository.create(account);
    }

    @Transactional
    public Long closeAccount(Long clientId, Long accountId) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Счет");
        account.verifyOwner(clientId);
        account.closeAccountCheck();
        List<Deposit> deposits = depositRepository.findByAccountId(accountId);
        deposits.forEach(Deposit::checkStatusForClosingDeposit);
        return accountRepository.delete(account);
    }
}
