package com.ltjeda.web.app.iobank.service;

import com.ltjeda.web.app.iobank.entity.*;
import com.ltjeda.web.app.iobank.repository.AccountRepository;
import com.ltjeda.web.app.iobank.repository.CardRepository;
import com.ltjeda.web.app.iobank.service.helper.AccountHelper;
import com.ltjeda.web.app.iobank.utils.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
    private final CardRepository cardRepository;
    private final AccountHelper accountHelper;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;


    public Card getCard(User user) {
        return cardRepository.findByOwnerUid(user.getUid()).orElseThrow();
    }

    public Card createCard(double amount, User user) throws Exception {
        if(amount < 2){
            throw new IllegalArgumentException("amount must be greater than 2");
        }
        if(!accountRepository.existsByCodeAndOwnerUid("USD", user.getUid())){
            throw new IllegalArgumentException("account does not exist");
        }
        var usdAccount = accountRepository.findByCodeAndOwnerUid("USD", user.getUid()).orElseThrow();
        accountHelper.validateSufficientFunds(usdAccount, amount);
        usdAccount.setBalance(usdAccount.getBalance() - amount);
        long cardNumber;
        do{
            cardNumber = generateCardNumber();
        }while (cardRepository.existsByCardNumber(cardNumber));
        Card card = Card.builder()
                .cardHolder(user.getFirstName() + " " + user.getLastName())
                .cardNumber(cardNumber)
                .owner(user)
                .exp(LocalDateTime.now().plusYears(3))
                .cvv(RandomUtil.generateRandom(3).toString())
                .balance(amount - 1)
                .build();
        card = cardRepository.save(card);
        transactionService.createAccountTransaction(1, Type.WITHDRAW, 0.00, user, usdAccount);
        transactionService.createAccountTransaction(amount - 1, Type.WITHDRAW, 0.00, user, usdAccount);
        transactionService.createCardTransaction(amount, Type.CREDIT, 0.00, user, card);
        accountHelper.save(usdAccount);
        return card;
    }

    public Transaction creditCard(Double amount, User user) {
        Account usdAccount = accountRepository.findByCodeAndOwnerUid("USD", user.getUid()).orElseThrow();
        usdAccount.setBalance(usdAccount.getBalance() - amount);
        transactionService.createAccountTransaction(amount, Type.WITHDRAW, 0.00, user, usdAccount);
        Card card = user.getCard();
        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
        return transactionService.createCardTransaction(amount, Type.CREDIT, 0.00, user, card);
    }

    public Transaction debitCard(double amount, User user) {
        Account usdAccount = accountRepository.findByCodeAndOwnerUid("USD", user.getUid()).orElseThrow();
        usdAccount.setBalance(usdAccount.getBalance() + amount);
        transactionService.createAccountTransaction(amount, Type.DEPOSIT, 0.00, user, usdAccount);
        Card card = user.getCard();
        card.setBalance(card.getBalance() - amount);
        cardRepository.save(card);
        return transactionService.createCardTransaction(amount, Type.DEBIT, 0.00, user, card);
    }

    private long generateCardNumber() {
        return RandomUtil.generateRandom(16);
    }
}
