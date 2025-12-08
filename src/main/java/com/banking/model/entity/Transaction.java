package com.banking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction Entity - All banking transactions
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_ref", columnList = "transactionReference"),
    @Index(name = "idx_transaction_date", columnList = "transactionDate"),
    @Index(name = "idx_stan", columnList = "stan")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionReference;

    @Column(unique = true)
    private String stan; // System Trace Audit Number (ISO8583)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    private String toAccountNumber; // For external transfers
    private String toIban;
    private String toBankName;
    private String toBankCode;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionChannel channel;

    private String description;
    private String remarks;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    private LocalDateTime completedDate;

    // ISO8583 specific fields
    private String mti; // Message Type Indicator
    private String processingCode;
    private String retrievalReferenceNumber;
    private String authorizationCode;
    private String responseCode;

    // Fee and charges
    private BigDecimal fee;
    private BigDecimal tax;

    // IBFT specific fields
    private String ibftReference;
    private String participantCode;

    // Raast specific fields
    private String raastTransactionId;
    private String raastPaymentId;

    // ATM specific fields
    private String atmId;
    private String terminalId;
    private String cardNumber; // Masked

    // Balance fields
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        IBFT,
        RAAST,
        ATM_WITHDRAWAL,
        ATM_DEPOSIT,
        POS_PURCHASE,
        CARD_PAYMENT,
        BILL_PAYMENT,
        SALARY_CREDIT,
        INTEREST_CREDIT,
        FEE_DEBIT,
        REVERSAL
    }

    public enum TransactionStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        REVERSED,
        CANCELLED,
        TIMEOUT
    }

    public enum TransactionChannel {
        BRANCH,
        ATM,
        MOBILE,
        INTERNET,
        AGENT,
        API,
        INTERNAL
    }
}
