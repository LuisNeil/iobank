package com.ltjeda.web.app.iobank.controllers;

import com.ltjeda.web.app.iobank.dto.AccountDto;
import com.ltjeda.web.app.iobank.dto.ConvertDto;
import com.ltjeda.web.app.iobank.dto.TransferDto;
import com.ltjeda.web.app.iobank.entity.Account;
import com.ltjeda.web.app.iobank.entity.Transaction;
import com.ltjeda.web.app.iobank.entity.User;
import com.ltjeda.web.app.iobank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountDto, Authentication authentication) throws Exception {
        var user = (User)authentication.getPrincipal();
        return ResponseEntity.ok(accountService.createAccount(accountDto, user));
    }

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(Authentication authentication) {
        var user = (User)authentication.getPrincipal();
        return ResponseEntity.ok(accountService.getUserAccounts(user.getUid()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferFunds(@RequestBody TransferDto transferDto, Authentication authentication) throws Exception {
        var user = (User)authentication.getPrincipal();
        return ResponseEntity.ok(accountService.transferFunds(transferDto, user));
    }

    @GetMapping("/rates")
    public ResponseEntity<Map<String, Double>> getExchangeRates(){
        return ResponseEntity.ok(accountService.getExchangeRates());
    }

    @PostMapping("/convert")
    public ResponseEntity<Transaction> convertCurrency(@RequestBody ConvertDto convertDto, Authentication authentication) throws Exception {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.convertCurrency(convertDto, user));
    }
}
