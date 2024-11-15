package com.ltjeda.web.app.iobank.controllers;

import com.ltjeda.web.app.iobank.entity.Transaction;
import com.ltjeda.web.app.iobank.entity.User;
import com.ltjeda.web.app.iobank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestParam Integer page, Authentication authentication) {
        return ResponseEntity.ok(transactionService.getAllTransactions(page,(User) authentication.getPrincipal()));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCardId(@PathVariable String cardId, @RequestParam Integer page, Authentication authentication) {
        return ResponseEntity.ok(transactionService.getTransactionsByCardId(cardId, (User) authentication.getPrincipal(), page));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable String accountId, @RequestParam Integer page, Authentication authentication) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId, (User) authentication.getPrincipal(), page));
    }
}
