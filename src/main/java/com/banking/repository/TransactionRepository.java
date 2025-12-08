package com.banking.repository;

import com.banking.model.entity.Account;
import com.banking.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(String transactionReference);

    Optional<Transaction> findByStan(String stan);

    List<Transaction> findByFromAccount(Account account);

    List<Transaction> findByToAccount(Account account);

    Page<Transaction> findByFromAccountOrToAccount(Account fromAccount, Account toAccount, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.id = ?1 OR t.toAccount.id = ?1) AND t.transactionDate BETWEEN ?2 AND ?3")
    List<Transaction> findAccountTransactionsBetweenDates(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByStatus(Transaction.TransactionStatus status);

    List<Transaction> findByTransactionType(Transaction.TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.transactionType = ?1 AND t.status = ?2 AND t.transactionDate >= ?3")
    List<Transaction> findRecentTransactionsByTypeAndStatus(
        Transaction.TransactionType type,
        Transaction.TransactionStatus status,
        LocalDateTime since
    );

    boolean existsByTransactionReference(String transactionReference);

    boolean existsByStan(String stan);
}
