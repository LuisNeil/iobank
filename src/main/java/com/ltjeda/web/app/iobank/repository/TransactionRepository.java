package com.ltjeda.web.app.iobank.repository;

import com.ltjeda.web.app.iobank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findAllByOwnerUid(String ownerId, Pageable pageable);
    Page<Transaction> findAllByCardCardIdAndOwnerUid(String cardId, String ownerId, Pageable pageable);
    Page<Transaction> findAllByAccountAccountIdAndOwnerUid(String accountId, String ownerId, Pageable pageable);
}
