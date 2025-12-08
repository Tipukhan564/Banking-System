package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class for Enterprise Banking System
 *
 * Features:
 * - Core Banking Operations
 * - ISO8583 Message Processing
 * - Real-time Socket Communication (MINA/Netty)
 * - Kafka Message Queue Integration
 * - SOAP/REST API Integration
 * - Transaction Processing (IBFT, Raast, ATM)
 * - KMS Encryption
 * - CSV Bulk Processing
 * - Reporting Module
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BankingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingSystemApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Enterprise Banking System Started");
        System.out.println("  Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("  API Docs: http://localhost:8080/api-docs");
        System.out.println("  ISO8583 Server: Port 8583");
        System.out.println("  Netty Server: Port 8584");
        System.out.println("===========================================");
    }
}
