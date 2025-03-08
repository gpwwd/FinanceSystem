package com.financialsystem.service;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.Deposit;
import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.repository.AccountRepository;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.repository.user.ClientRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Long createAccount(Long clientId, Long bankId, Currency currency, boolean isAccountForSalary
    ) {
        entityFinder.findEntityById(clientId, clientRepository, "Клиент");
        Account account = Account.create(clientId, bankId, currency, isAccountForSalary);
        return accountRepository.create(account);
    }

    @Transactional
    public Long closeAccount(Long clientId, Long accountId) {
        Account account = entityFinder.findEntityById(accountId, accountRepository, "Аккаунт");
        account.verifyOwner(clientId);
        account.closeAccountCheck();
        List<Deposit> deposits = depositRepository.findByAccountId(accountId);
        deposits.forEach(Deposit::checkStatusForClosingDeposit);
        return accountRepository.delete(account);
    }
}
