package com.banking.repository;

import com.banking.model.entity.Account;
import com.banking.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Account entity
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByIban(String iban);

    List<Account> findByCustomer(Customer customer);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByStatus(Account.AccountStatus status);

    @Query("SELECT a FROM Account a WHERE a.customer.id = ?1 AND a.status = ?2")
    List<Account> findActiveAccountsByCustomerId(Long customerId, Account.AccountStatus status);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByIban(String iban);
}
