package com.financialsystem.service;

import com.financialsystem.domain.model.FinancialEntity;
import com.financialsystem.domain.model.Loan;
import com.financialsystem.domain.model.account.Account;
import com.financialsystem.domain.model.deposit.Deposit;
import com.financialsystem.domain.model.transaction.Transaction;
import com.financialsystem.domain.model.transaction.TransactionType;
import com.financialsystem.exception.custom.BadRequestException;
import com.financialsystem.exception.custom.NotFoundException;
import com.financialsystem.repository.DepositRepository;
import com.financialsystem.repository.TransactionRepository;
import com.financialsystem.repository.account.AccountRepository;
import com.financialsystem.repository.loan.LoanRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionsService {
    private final TransactionRepository transactionsRepository;
    private final DepositRepository depositsRepository;
    private final AccountRepository accountsRepository;
    private final LoanRepository loansRepository;
    private final EntityFinder entityFinder;

    @Autowired
    public TransactionsService(
            TransactionRepository transactionsRepository,
            DepositRepository depositsRepository,
            AccountRepository accountsRepository,
            LoanRepository loansRepository,
            EntityFinder entityFinder) {
        this.transactionsRepository = transactionsRepository;
        this.depositsRepository = depositsRepository;
        this.accountsRepository = accountsRepository;
        this.loansRepository = loansRepository;
        this.entityFinder = entityFinder;
    }

    public List<Transaction> getAll() {
        return transactionsRepository.findAll();
    }

//    public List<Transaction> getAllByUserId(Long id) {
//        return transactionsRepository.findAllByUserId(id);
//    }
//
//    public List<Transaction> getAllByEntityId(Long id, TransactionType type) {
//        return transactionsRepository.findAllByEntityId(id, type);
//    }

    public Transaction getById(Long id) {
        return entityFinder.findEntityById(id, transactionsRepository, "Транзакция");
    }

    @Transactional
    public Long revertTransaction(Long id) {
        var originalTransaction = getById(id);

        var amount = originalTransaction.getAmount();
        var fromId = originalTransaction.getFromEntityId();
        var fromType = originalTransaction.getFromType();
        var toId = originalTransaction.getToEntityId();
        var toType = originalTransaction.getToType();

        if (toType.equals(TransactionType.EXTERNAL) || fromType.equals(TransactionType.EXTERNAL))
            throw new BadRequestException("Unable to undo transaction with EXTERNAL type.");

        if (originalTransaction.getRevertTransactionId() != null)
            throw new BadRequestException("Transaction has already been reversed");

        var fromEntity = findEntity(fromId, fromType);
        var toEntity = findEntity(toId, toType);

        fromEntity.replenish(amount);
        toEntity.withdraw(amount);

        var revertTransaction = Transaction.create(
                toId, toType, fromId, fromType, amount
        );

        var revertId = transactionsRepository.create(revertTransaction);
        originalTransaction.setRevertTransactionId(revertId);
        transactionsRepository.update(originalTransaction);

        updateEntity(fromEntity, fromType);
        updateEntity(toEntity, toType);

        return revertId;
    }

    private FinancialEntity findEntity(Long id, TransactionType type) {
        return switch (type) {
            case ACCOUNT -> accountsRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Account not found"));
            case LOAN -> loansRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Loan not found"));
            case DEPOSIT -> depositsRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Deposit not found"));
            default -> throw new IllegalArgumentException("Unsupported entity type");
        };
    }

    private void updateEntity(FinancialEntity entity, TransactionType type) {
        switch (type) {
            case ACCOUNT -> accountsRepository.update( (Account) entity);
            case LOAN -> loansRepository.update( (Loan) entity);
            case DEPOSIT -> depositsRepository.update( (Deposit) entity);
            default -> throw new IllegalArgumentException("Unsupported entity type");
        }
    }
}
