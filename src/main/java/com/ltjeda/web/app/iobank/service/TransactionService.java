package com.ltjeda.web.app.iobank.service;

import com.ltjeda.web.app.iobank.entity.*;
import com.ltjeda.web.app.iobank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions(Integer page, User user) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return transactionRepository.findAllByOwnerUid(user.getUid(), pageable).getContent();
    }

    public List<Transaction> getTransactionsByCardId(String cardId, User user, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return transactionRepository.findAllByCardCardIdAndOwnerUid(cardId, user.getUid(), pageable).getContent();
    }

    public List<Transaction> getTransactionsByAccountId(String accountId, User user, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return transactionRepository.findAllByCardCardIdAndOwnerUid(accountId, user.getUid(), pageable).getContent();
    }

    public Transaction createAccountTransaction(double amount, Type type, double transactionFee, User user, Account account) {
        Transaction transaction = Transaction.builder()
                .transactionFee(transactionFee)
                .amount(amount)
                .type(type)
                .status(Status.COMPLETED)
                .owner(user)
                .account(account)
                .build();
        return transactionRepository.save(transaction);
    }

    public Transaction createCardTransaction(double amount, Type type, double transactionFee, User user, Card card) {
        Transaction transaction = Transaction.builder()
                .transactionFee(transactionFee)
                .amount(amount)
                .type(type)
                .status(Status.COMPLETED)
                .owner(user)
                .card(card)
                .build();
        return transactionRepository.save(transaction);
    }
}
