package com.banking.controller;

import com.banking.model.entity.Account;
import com.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> request) {
        Long customerId = Long.valueOf(request.get("customerId").toString());
        Account.AccountType accountType = Account.AccountType.valueOf(request.get("accountType").toString());
        String currency = request.getOrDefault("currency", "PKR").toString();

        Account account = accountService.createAccount(customerId, accountType, currency);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{accountNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Account>> getCustomerAccounts(@PathVariable Long customerId) {
        List<Account> accounts = accountService.getCustomerAccounts(customerId);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/{accountNumber}/freeze")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> freezeAccount(@PathVariable String accountNumber) {
        accountService.freezeAccount(accountNumber);
        return ResponseEntity.ok("Account frozen successfully");
    }

    @PostMapping("/{accountNumber}/unfreeze")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unfreezeAccount(@PathVariable String accountNumber) {
        accountService.unfreezeAccount(accountNumber);
        return ResponseEntity.ok("Account unfrozen successfully");
    }
}
