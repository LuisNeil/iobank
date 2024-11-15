package com.ltjeda.web.app.iobank.controllers;

import com.ltjeda.web.app.iobank.entity.Card;
import com.ltjeda.web.app.iobank.entity.Transaction;
import com.ltjeda.web.app.iobank.entity.User;
import com.ltjeda.web.app.iobank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Card> getCard(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.getCard(user));
    }

    @PostMapping("/create")
    public ResponseEntity<Card> createCard(@RequestParam double amount, Authentication authentication) throws Exception {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.createCard(amount, user));
    }

    @PostMapping("/credit")
    public ResponseEntity<Transaction> creditCard(@RequestParam double amount, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.creditCard(amount, user));
    }

    @PostMapping("/debit")
    public ResponseEntity<Transaction> debitCard(@RequestParam double amount, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(cardService.debitCard(amount, user));
    }
}
