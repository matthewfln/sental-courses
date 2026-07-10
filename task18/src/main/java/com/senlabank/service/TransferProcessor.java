package com.senlabank.service;
import com.senlabank.model.Transfer;
import com.senlabank.model.Account;

public interface TransferProcessor {
    void processTransfer(Transfer transfer);
    void executeSuccessfulTransfer(Transfer transfer, Account fromAccount, Account toAccount);
    void saveFailedTransfer(Transfer transfer);
}