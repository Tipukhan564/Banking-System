package com.banking.service;

import com.banking.model.entity.Account;
import com.banking.model.entity.Customer;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Account Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    /**
     * Create new account
     */
    @Transactional
    public Account createAccount(Long customerId, Account.AccountType accountType, String currency) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .iban(generateIBAN())
                .customer(customer)
                .accountType(accountType)
                .balance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .currency(currency != null ? currency : "PKR")
                .status(Account.AccountStatus.ACTIVE)
                .branchCode("001")
                .branchName("Main Branch")
                .interestRate(accountType == Account.AccountType.SAVINGS ? BigDecimal.valueOf(5.0) : BigDecimal.ZERO)
                .build();

        account = accountRepository.save(account);
        log.info("Created new account {} for customer {}", account.getAccountNumber(), customerId);

        return account;
    }

    /**
     * Get account by account number
     */
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    /**
     * Get account by IBAN
     */
    public Account getAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Account not found with IBAN: " + iban));
    }

    /**
     * Get all accounts for customer
     */
    public List<Account> getCustomerAccounts(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    /**
     * Update account balance
     */
    @Transactional
    public Account updateBalance(String accountNumber, BigDecimal amount, boolean isCredit) {
        Account account = getAccountByNumber(accountNumber);

        if (isCredit) {
            account.setBalance(account.getBalance().add(amount));
            account.setAvailableBalance(account.getAvailableBalance().add(amount));
        } else {
            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(amount));
            account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
        }

        account.setLastTransactionDate(LocalDateTime.now());
        return accountRepository.save(account);
    }

    /**
     * Freeze account
     */
    @Transactional
    public void freezeAccount(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        account.setStatus(Account.AccountStatus.FROZEN);
        accountRepository.save(account);
        log.info("Account {} frozen", accountNumber);
    }

    /**
     * Unfreeze account
     */
    @Transactional
    public void unfreezeAccount(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        account.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(account);
        log.info("Account {} unfrozen", accountNumber);
    }

    /**
     * Generate unique account number
     */
    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%020d", new Random().nextLong() & Long.MAX_VALUE);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    /**
     * Generate IBAN (Pakistan format: PK + 2 check digits + 4 bank code + 16 account number)
     */
    private String generateIBAN() {
        String iban;
        do {
            String bankCode = "ABCD"; // Bank code
            String accountNumber = String.format("%016d", new Random().nextLong() & Long.MAX_VALUE);
            String checkDigits = "00"; // Simplified - in production, calculate actual check digits
            iban = "PK" + checkDigits + bankCode + accountNumber;
        } while (accountRepository.existsByIban(iban));
        return iban;
    }
}
