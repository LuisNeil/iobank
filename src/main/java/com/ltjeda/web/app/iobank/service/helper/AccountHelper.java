package com.ltjeda.web.app.iobank.service.helper;

import com.ltjeda.web.app.iobank.dto.AccountDto;
import com.ltjeda.web.app.iobank.dto.ConvertDto;
import com.ltjeda.web.app.iobank.entity.*;
import com.ltjeda.web.app.iobank.repository.AccountRepository;
import com.ltjeda.web.app.iobank.repository.TransactionRepository;
import com.ltjeda.web.app.iobank.service.ExchangeRateService;
import com.ltjeda.web.app.iobank.service.TransactionService;
import com.ltjeda.web.app.iobank.utils.RandomUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class AccountHelper {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;

    private final Map<String, String> CURRENCIES = Map.of(
            "USD", "United States Dollar",
            "EUR", "Euro",
            "GBP", "British Pound",
            "JPY", "Japanese Yen",
            "NGN", "Nigerian Naira",
            "INR", "Indian Rupee"
    );
    private final TransactionService transactionService;


    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        long accountNumber;
        try {
            validateAccountNonExistsForUser(accountDto.getCode(), user.getUid());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        do {
            accountNumber = RandomUtil.generateRandom(10);
        }while (accountRepository.existsByAccountNumber(accountNumber));

        var account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(user.getFirstName() + " " + user.getLastName())
                .balance(1000.0)
                .owner(user)
                .code(accountDto.getCode())
                .symbol(accountDto.getSymbol())
                .label(CURRENCIES.get(accountDto.getCode()))
                .currency(CURRENCIES.get(accountDto.getCode()))
                .build();
        return accountRepository.save(account);
    }

    public void validateAccountNonExistsForUser(String code, String uid) throws Exception {
        if(accountRepository.existsByCodeAndOwnerUid(code, uid)){
            throw new Exception("Account of this type already exists for this user");
        }
    }

    public Transaction performTransfer(Account senderAccount, Account receiverAccount, double amount, User user) throws Exception {
        validateSufficientFunds(senderAccount, (amount *1.01));
        senderAccount.setBalance(senderAccount.getBalance() - (amount*1.01));
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        accountRepository.saveAll(List.of(receiverAccount, senderAccount));
        Transaction senderTransaction = transactionService.createAccountTransaction(amount, Type.WITHDRAW, amount * .01, user, senderAccount);
        Transaction receiverTransaction = transactionService.createAccountTransaction(amount, Type.DEPOSIT, 0.00, user, receiverAccount);

        return senderTransaction;
    }

    public void validateSufficientFunds(Account account, double amount) throws Exception {
        if(account.getBalance() < amount){
            throw new OperationNotSupportedException("Insufficient funds in account");
        }
    }

    public void validateAmount(double amount) throws Exception {
        if(amount < 0){
            throw new OperationNotSupportedException("Insufficient funds");
        }
    }

    public void validateDifferentCurrencyType(ConvertDto convertDto) throws Exception{
        if(convertDto.getToCurrency().equals(convertDto.getFromCurrency())){
            throw new IllegalArgumentException("Conversion between the same type of currency is not allowed");
        }
    }

    public void validateAccountOwnership(ConvertDto convertDto, String uid) throws Exception{
        accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(), uid);
        accountRepository.findByCodeAndOwnerUid(convertDto.getToCurrency(), uid);
    }

    public void  validateConversion(ConvertDto convertDto, String uid) throws Exception{
        validateDifferentCurrencyType(convertDto);
        validateAccountOwnership(convertDto, uid);
        validateAmount(convertDto.getAmount());
        validateSufficientFunds(accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(), uid).get(), convertDto.getAmount());
    }

    public Transaction convertCurrency(ConvertDto convertDto, User user) throws Exception{
        validateConversion(convertDto, user.getUid());
        Map<String, Double> rates = exchangeRateService.getRates();
        Double sendingRates = rates.get(convertDto.getFromCurrency());
        Double receivingRates = rates.get(convertDto.getToCurrency());
        double computedAmount = (receivingRates / sendingRates) * convertDto.getAmount();
        Account fromAccount = accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(), user.getUid()).orElseThrow();
        Account toAccount = accountRepository.findByCodeAndOwnerUid(convertDto.getToCurrency(), user.getUid()).orElseThrow();
        fromAccount.setBalance(fromAccount.getBalance() - (convertDto.getAmount() - 1.01));
        toAccount.setBalance(toAccount.getBalance() + computedAmount);
        accountRepository.saveAll(List.of(fromAccount, toAccount));

        Transaction fromAccountTransaction = transactionService.createAccountTransaction(convertDto.getAmount(), Type.CONVERSION, convertDto.getAmount() * 0.01, user, fromAccount);
        Transaction toAccountTransaction = transactionService.createAccountTransaction(computedAmount, Type.DEPOSIT, 0.00, user, toAccount);

        return fromAccountTransaction;
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

}
