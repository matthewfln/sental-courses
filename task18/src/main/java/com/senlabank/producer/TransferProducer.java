package com.senlabank.producer;

import org.springframework.transaction.annotation.Transactional;
import com.senlabank.model.Account;
import com.senlabank.model.Transfer;
import com.senlabank.repository.AccountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.annotation.Profile;

@Service
@Profile("producer")
public class TransferProducer {

    private static final Logger LOGGER = LogManager.getLogger(TransferProducer.class);
    private static final int MAX_ACCOUNTS = 1000;
    private static final int MAX_AMOUNT = 5000;
    private static final long MAX_ID = 1_000_000_000L;
    private static final String TOPIC_NAME = "bank-transfers";

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, Transfer> kafkaTemplate;
    private final Map<Long, Account> localAccountCache = new ConcurrentHashMap<>();

    public TransferProducer(AccountRepository accountRepository, KafkaTemplate<String, Transfer> kafkaTemplate) {
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void initAccounts() {
        LOGGER.info("Проверка и инициализация счетов...");
        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) {
            LOGGER.info("БД пуста. Генерируем {} счетов...", MAX_ACCOUNTS);
            List<Account> accountsToSave = new ArrayList<>(MAX_ACCOUNTS);

            for (long i = 1; i <= MAX_ACCOUNTS; i++) {
                Account account = new Account(i, new BigDecimal("10000.00"));
                accountsToSave.add(account);
                localAccountCache.put(i, account);
            }

            // Оптимизация: сохраняем все счета одним батчем
            accountRepository.saveAll(accountsToSave);
        } else {
            LOGGER.info("Загрузка существующих счетов из БД в память...");
            for (Account account : accounts) {
                localAccountCache.put(account.getId(), account);
            }
        }
    }

    @Scheduled(fixedDelay = 200)
    @Transactional("kafkaTransactionManager")
    public void generateAndSendTransfer() {
        if (localAccountCache.isEmpty()) {
            return;
        }

        java.util.Random random = new java.util.Random();

        long fromId = random.nextInt(MAX_ACCOUNTS) + 1;
        long toId = random.nextInt(MAX_ACCOUNTS) + 1;

        while (fromId == toId) {
            toId = random.nextInt(MAX_ACCOUNTS) + 1;
        }

        long transferId = random.nextInt((int) MAX_ID) + 1;
        int randomAmount = random.nextInt(MAX_AMOUNT) + 1;
        BigDecimal amount = new BigDecimal(randomAmount);

        Transfer transfer = new Transfer(transferId, fromId, toId, amount, null);

        LOGGER.info("Продюсер отправляет перевод ID: {} ({} -> {}, сумма: {})", transferId, fromId, toId, amount);

        // Отправляем в Kafka (без ключа, чтобы сообщения равномерно распределялись по всем партициям)
        kafkaTemplate.send(TOPIC_NAME, null, transfer);
    }
}
