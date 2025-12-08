package com.banking.service;

import com.banking.model.entity.Account;
import com.banking.model.entity.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Transaction Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    

    /**
     * Process internal fund transfer
     */
    @Transactional
    public Transaction processTransfer(String fromAccountNumber, String toAccountNumber,
                                      BigDecimal amount, String description) {

        Account fromAccount = accountService.getAccountByNumber(fromAccountNumber);
        Account toAccount = accountService.getAccountByNumber(toAccountNumber);

        if (fromAccount.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Source account is not active");
        }

        if (toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Destination account is not active");
        }

        Transaction transaction = Transaction.builder()
                .transactionReference(generateTransactionReference())
                .stan(generateSTAN())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .currency(fromAccount.getCurrency())
                .transactionType(Transaction.TransactionType.TRANSFER)
                .status(Transaction.TransactionStatus.PROCESSING)
                .channel(Transaction.TransactionChannel.INTERNET)
                .description(description)
                .balanceBefore(fromAccount.getBalance())
                .fee(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .build();

        transaction = transactionRepository.save(transaction);

        try {
            // Update balances
            accountService.updateBalance(fromAccountNumber, amount, false);
            accountService.updateBalance(toAccountNumber, amount, true);

            // Update transaction status
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedDate(LocalDateTime.now());
            transaction.setBalanceAfter(fromAccount.getBalance().subtract(amount));
            transaction = transactionRepository.save(transaction);


            log.info("Transfer completed: {} from {} to {}", amount, fromAccountNumber, toAccountNumber);

        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            log.error("Transfer failed", e);
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }

        return transaction;
    }

    /**
     * Process deposit
     */
    @Transactional
    public Transaction processDeposit(String accountNumber, BigDecimal amount, String description) {
        Account account = accountService.getAccountByNumber(accountNumber);

        Transaction transaction = Transaction.builder()
                .transactionReference(generateTransactionReference())
                .stan(generateSTAN())
                .toAccount(account)
                .amount(amount)
                .currency(account.getCurrency())
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.TransactionStatus.PROCESSING)
                .channel(Transaction.TransactionChannel.BRANCH)
                .description(description)
                .balanceBefore(account.getBalance())
                .fee(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .build();

        transaction = transactionRepository.save(transaction);

        try {
            accountService.updateBalance(accountNumber, amount, true);

            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedDate(LocalDateTime.now());
            transaction.setBalanceAfter(account.getBalance().add(amount));
            transaction = transactionRepository.save(transaction);

            log.info("Deposit completed: {} to {}", amount, accountNumber);

        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Deposit failed: " + e.getMessage());
        }

        return transaction;
    }

    /**
     * Process withdrawal
     */
    @Transactional
    public Transaction processWithdrawal(String accountNumber, BigDecimal amount, String description) {
        Account account = accountService.getAccountByNumber(accountNumber);

        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction transaction = Transaction.builder()
                .transactionReference(generateTransactionReference())
                .stan(generateSTAN())
                .fromAccount(account)
                .amount(amount)
                .currency(account.getCurrency())
                .transactionType(Transaction.TransactionType.WITHDRAWAL)
                .status(Transaction.TransactionStatus.PROCESSING)
                .channel(Transaction.TransactionChannel.BRANCH)
                .description(description)
                .balanceBefore(account.getBalance())
                .fee(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .build();

        transaction = transactionRepository.save(transaction);

        try {
            accountService.updateBalance(accountNumber, amount, false);

            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedDate(LocalDateTime.now());
            transaction.setBalanceAfter(account.getBalance().subtract(amount));
            transaction = transactionRepository.save(transaction);

            log.info("Withdrawal completed: {} from {}", amount, accountNumber);

        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Withdrawal failed: " + e.getMessage());
        }

        return transaction;
    }

    /**
     * Get transaction by reference
     */
    public Transaction getTransactionByReference(String reference) {
        return transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + reference));
    }

    /**
     * Get account transactions
     */
    public List<Transaction> getAccountTransactions(String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return transactionRepository.findByFromAccount(account);
    }

    /**
     * Generate unique transaction reference
     */
    private String generateTransactionReference() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 16);
    }

    /**
     * Generate STAN (System Trace Audit Number)
     */
    private String generateSTAN() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
