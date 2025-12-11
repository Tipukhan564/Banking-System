package com.banking.service;

import com.banking.model.entity.Account;
import com.banking.model.entity.Customer;
import com.banking.repository.CustomerRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CSV Processing Service
 * Handles bulk customer and account creation from CSV files
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CSVProcessingService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;
    private final EncryptionService encryptionService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Process CSV file for bulk account opening
     * CSV Format: firstName,lastName,email,cnic,phoneNumber,dateOfBirth,accountType
     */
    @Transactional
    public List<Customer> processBulkAccountOpening(MultipartFile file) {
        List<Customer> createdCustomers = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();

            // Skip header row
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);

                try {
                    Customer customer = processCustomerRecord(record);
                    createdCustomers.add(customer);
                    log.info("Processed customer: {} {}", customer.getFirstName(), customer.getLastName());
                } catch (Exception e) {
                    log.error("Error processing record {}: {}", i, e.getMessage());
                }
            }

            log.info("Bulk account opening completed. Created {} customers", createdCustomers.size());

        } catch (Exception e) {
            log.error("Error processing CSV file", e);
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }

        return createdCustomers;
    }

    /**
     * Process individual customer record
     */
    private Customer processCustomerRecord(String[] record) {
        String firstName = record[0].trim();
        String lastName = record[1].trim();
        String email = record[2].trim();
        String cnic = record[3].trim();
        String phoneNumber = record[4].trim();
        LocalDate dateOfBirth = LocalDate.parse(record[5].trim());
        Account.AccountType accountType = Account.AccountType.valueOf(record[6].trim().toUpperCase());

        // Check if customer already exists
        if (customerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        if (customerRepository.existsByCnic(encryptionService.encrypt(cnic))) {
            throw new RuntimeException("CNIC already registered: " + cnic);
        }

        // Create customer
        Customer customer = Customer.builder()
                .customerId(generateCustomerId())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .cnic(encryptionService.encrypt(cnic))
                .phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth)
                .password(passwordEncoder.encode("Password123")) // Default password
                .status(Customer.CustomerStatus.ACTIVE)
                .type(Customer.CustomerType.INDIVIDUAL)
                .nadraVerified(false)
                .build();

        customer = customerRepository.save(customer);

        // Create account
        accountService.createAccount(customer.getId(), accountType, "PKR");

        return customer;
    }

    /**
     * Generate customer ID
     */
    private String generateCustomerId() {
        return "CUST" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    /**
     * Validate CSV format
     */
    public boolean validateCSVFormat(MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();

            if (records.isEmpty()) {
                return false;
            }

            // Check header
            String[] header = records.get(0);
            return header.length >= 7 &&
                   "firstName".equalsIgnoreCase(header[0].trim()) &&
                   "lastName".equalsIgnoreCase(header[1].trim()) &&
                   "email".equalsIgnoreCase(header[2].trim());

        } catch (Exception e) {
            log.error("Error validating CSV file", e);
            return false;
        }
    }
}
