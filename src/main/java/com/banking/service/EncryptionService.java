package com.banking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encryption Service with KMS Simulation
 * Handles encryption/decryption of sensitive data like CNIC, Card Numbers, CVV
 */
@Service
public class EncryptionService {

    @Value("${encryption.algorithm:AES}")
    private String algorithm;

    @Value("${encryption.key.size:256}")
    private int keySize;

    @Value("${kms.enabled:true}")
    private boolean kmsEnabled;

    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    // In production, this would be fetched from actual KMS (AWS KMS, Azure Key Vault, etc.)
    private final SecretKey secretKey;

    public EncryptionService() {
        this.secretKey = generateKey();
    }

    /**
     * Generate a new encryption key (KMS simulation)
     */
    private SecretKey generateKey() {
    try {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // 128 or 256 bits
        return keyGenerator.generateKey();
    } catch (Exception e) {
        throw new RuntimeException("Error generating encryption key", e);
    }
}


    /**
     * Encrypt data using KMS
     */
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypt data using KMS
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Mask sensitive data (e.g., card numbers, CNIC)
     */
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Mask CNIC
     */
    public String maskCnic(String cnic) {
        if (cnic == null || cnic.length() < 8) {
            return cnic;
        }
        return "*****" + cnic.substring(cnic.length() - 4);
    }

    /**
     * Validate if data is encrypted
     */
    public boolean isEncrypted(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        try {
            Base64.getDecoder().decode(data);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
