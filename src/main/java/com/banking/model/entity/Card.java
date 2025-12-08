package com.banking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Card Entity - Debit/Credit cards
 */
@Entity
@Table(name = "cards", indexes = {
    @Index(name = "idx_card_number", columnList = "cardNumber")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 19)
    private String cardNumber; // Encrypted

    @Column(nullable = false)
    private String cardHolderName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(nullable = false)
    private String cvv; // Encrypted

    @Column(nullable = false)
    private LocalDate expiryDate;

    private LocalDate issueDate;

    @Column(nullable = false)
    private String pin; // Hashed

    // Limits
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private BigDecimal perTransactionLimit;

    // Daily usage tracking
    private BigDecimal dailyUsage;
    private LocalDate lastUsageResetDate;

    // Security
    private Integer failedPinAttempts;
    private Boolean isBlocked;
    private LocalDateTime blockedAt;
    private String blockedReason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum CardType {
        DEBIT, CREDIT, PREPAID
    }

    public enum CardBrand {
        VISA, MASTERCARD, UNIONPAY, AMEX
    }

    public enum CardStatus {
        ACTIVE, INACTIVE, BLOCKED, EXPIRED, LOST, STOLEN
    }
}
