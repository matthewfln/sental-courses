package com.senlabank.service;

import com.senlabank.model.Account;
import com.senlabank.model.Transfer;
import com.senlabank.model.TransferStatus;
import com.senlabank.repository.AccountRepository;
import com.senlabank.repository.TransferRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

import java.util.Optional;

@Service
public class TransferProcessorImpl implements TransferProcessor {

    private static final Logger LOGGER = LogManager.getLogger(TransferProcessorImpl.class);

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final TransferProcessor self;

    public TransferProcessorImpl(AccountRepository accountRepository,
                                 TransferRepository transferRepository,
                                 @Lazy TransferProcessor self) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.self = self;
    }

    @Override
    public void processTransfer(Transfer transfer) {
        Optional<Account> fromAccountOpt = accountRepository.findById(transfer.getFromAccountId());
        Optional<Account> toAccountOpt = accountRepository.findById(transfer.getToAccountId());

        // 1. Проверка существования счетов
        if (!fromAccountOpt.isPresent() || !toAccountOpt.isPresent()) {
            LOGGER.error("Ошибка: Один или оба счета не найдены в БД. Перевод ID: " + transfer.getId());
            return;
        }

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        // 2. Проверка баланса
        if (fromAccount.getCurrentBalance().compareTo(transfer.getAmount()) < 0) {
            LOGGER.error("Ошибка: Недостаточно средств на счете ID: " + fromAccount.getId() + " для перевода ID: " + transfer.getId());
            return;
        }

        // 3. Выполнение перевода в транзакции
        try {
            self.executeSuccessfulTransfer(transfer, fromAccount, toAccount);
        } catch (Exception exception) {
            LOGGER.error("Транзакция упала для перевода ID: " + transfer.getId() + ". Сохраняем статус FAILED.", exception);
            self.saveFailedTransfer(transfer);
        }
    }

    @Transactional
    public void executeSuccessfulTransfer(Transfer transfer, Account fromAccount, Account toAccount) {
        fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(transfer.getAmount()));
        toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(transfer.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transfer.setStatus(TransferStatus.DONE);
        transferRepository.save(transfer);

        LOGGER.info("Перевод ID: " + transfer.getId() + " успешно завершен.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransfer(Transfer transfer) {
        transfer.setStatus(TransferStatus.FAILED);
        transferRepository.save(transfer);
    }
}