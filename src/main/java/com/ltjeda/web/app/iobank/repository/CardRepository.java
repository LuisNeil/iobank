package com.ltjeda.web.app.iobank.repository;

import com.ltjeda.web.app.iobank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    Optional<Card> findByOwnerUid(String uid);

    boolean existsByCardNumber(long cardNumber);
}

