package com.ltjeda.web.app.iobank.service;

import com.ltjeda.web.app.iobank.dto.AccountDto;
import com.ltjeda.web.app.iobank.dto.ConvertDto;
import com.ltjeda.web.app.iobank.dto.TransferDto;
import com.ltjeda.web.app.iobank.entity.Account;
import com.ltjeda.web.app.iobank.entity.Transaction;
import com.ltjeda.web.app.iobank.entity.User;
import com.ltjeda.web.app.iobank.repository.AccountRepository;
import com.ltjeda.web.app.iobank.service.helper.AccountHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHelper accountHelper;
    private final ExchangeRateService exchangeRateService;


    public Account createAccount(AccountDto accountDto, User user) throws Exception {
        return accountHelper.createAccount(accountDto, user);

    }

    public List<Account> getUserAccounts(String uid) {
        return accountRepository.findAllByOwnerUid(uid);
    }

    public Transaction transferFunds(TransferDto transferDto, User user) throws Exception {
        Account senderAccount = accountRepository.findByCodeAndOwnerUid(transferDto.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account or type of  currency does not exist for user"));
        Account receiverAccount = accountRepository.findByAccountNumber(transferDto.getRecipientAccountNumber()).orElseThrow();
        return accountHelper.performTransfer(senderAccount, receiverAccount, transferDto.getAmount(), user);

    }

    public Map<String, Double> getExchangeRates(){
        return exchangeRateService.getRates();
    }

    public Transaction convertCurrency(ConvertDto convertDto, User user) throws Exception {
        return accountHelper.convertCurrency(convertDto, user);
    }
}
