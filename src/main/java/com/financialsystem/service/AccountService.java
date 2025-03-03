package com.financialsystem.service;

import com.financialsystem.domain.model.Account;
import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.repository.AccountRepository;
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
    private final EntityFinder entityFinder;

    @Autowired
    public AccountService(AccountRepository accountRepository, EntityFinder entityFinder,
                          ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.entityFinder = entityFinder;
        this.clientRepository = clientRepository;
    }

    @Transactional
    @PreAuthorize("hasAuthority('CLIENT')")
    public Long createAccount(Long clientId, Long bankId, Currency currency, boolean isAccountForSalary // позже заменить на User из UserPrincipal.getUser()
    ) {
        Client client = entityFinder.findEntityById(clientId, clientRepository, "Клиент");
        Account account = Account.create(clientId, bankId, currency, isAccountForSalary);
        return accountRepository.create(account);
    }

//    @Transactional
//    public Long closeAccount(Long clientId, Long accountId) {
//        Client client = entityFinder.findEntityById(clientId, clientRepository, "Клиент");
//    }
}
