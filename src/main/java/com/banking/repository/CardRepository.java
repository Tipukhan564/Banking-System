package com.banking.repository;

import com.banking.model.entity.Card;
import com.banking.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Card entity
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByCustomer(Customer customer);

    List<Card> findByCustomerId(Long customerId);

    List<Card> findByStatus(Card.CardStatus status);

    @Query("SELECT c FROM Card c WHERE c.expiryDate < ?1")
    List<Card> findExpiredCards(LocalDate date);

    @Query("SELECT c FROM Card c WHERE c.expiryDate BETWEEN ?1 AND ?2")
    List<Card> findCardsExpiringBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByCardNumber(String cardNumber);
}
