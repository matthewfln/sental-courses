package com.senlabank.consumer;

import com.senlabank.model.Transfer;
import com.senlabank.service.TransferProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Component
@Profile("consumer")
public class TransferConsumer {

    private static final Logger LOGGER = LogManager.getLogger(TransferConsumer.class);
    private final TransferProcessor transferProcessor;

    public TransferConsumer(TransferProcessor transferProcessor) {
        this.transferProcessor = transferProcessor;
    }

    @KafkaListener(topics = "bank-transfers", groupId = "bank-consumer-group")
    public void listenAndProcess(java.util.List<Transfer> transfers) {
        LOGGER.info("Консюмер получил пачку сообщений. Размер пачки: " + transfers.size());

        for (Transfer transfer : transfers) {
            LOGGER.info("Начало обработки перевода ID: " + transfer.getId());
            transferProcessor.processTransfer(transfer);
        }
    }
}