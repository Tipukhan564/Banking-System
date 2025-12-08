package com.banking.controller;

import com.banking.model.dto.TransferRequest;
import com.banking.model.entity.Transaction;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> transfer(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionService.processTransfer(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> deposit(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionService.processDeposit(
                request.getToAccountNumber(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/withdrawal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> withdrawal(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionService.processWithdrawal(
                request.getFromAccountNumber(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{reference}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String reference) {
        Transaction transaction = transactionService.getTransactionByReference(reference);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable String accountNumber) {
        List<Transaction> transactions = transactionService.getAccountTransactions(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}
