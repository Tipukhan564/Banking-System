package com.banking.iso8583;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * ISO8583 Message Structure
 *
 * Common MTI (Message Type Indicator) values:
 * - 0100: Authorization Request
 * - 0110: Authorization Response
 * - 0200: Financial Transaction Request
 * - 0210: Financial Transaction Response
 * - 0400: Reversal Request
 * - 0410: Reversal Response
 * - 0800: Network Management Request
 * - 0810: Network Management Response
 */
@Data
public class ISO8583Message {

    private String mti; // Message Type Indicator
    private Map<Integer, String> dataElements;

    public ISO8583Message() {
        this.dataElements = new HashMap<>();
    }

    public ISO8583Message(String mti) {
        this.mti = mti;
        this.dataElements = new HashMap<>();
    }

    public void setField(int field, String value) {
        dataElements.put(field, value);
    }

    public String getField(int field) {
        return dataElements.get(field);
    }

    public boolean hasField(int field) {
        return dataElements.containsKey(field);
    }

    /**
     * Common ISO8583 Data Elements
     */
    public static class Fields {
        public static final int PAN = 2;                    // Primary Account Number
        public static final int PROCESSING_CODE = 3;        // Processing Code
        public static final int AMOUNT = 4;                 // Transaction Amount
        public static final int STAN = 11;                  // System Trace Audit Number
        public static final int LOCAL_TIME = 12;            // Local Transaction Time
        public static final int LOCAL_DATE = 13;            // Local Transaction Date
        public static final int EXPIRATION_DATE = 14;       // Card Expiration Date
        public static final int MERCHANT_TYPE = 18;         // Merchant Type
        public static final int POS_ENTRY_MODE = 22;        // POS Entry Mode
        public static final int FUNCTION_CODE = 24;         // Function Code
        public static final int POS_CONDITION_CODE = 25;    // POS Condition Code
        public static final int ACQUIRING_INST_ID = 32;     // Acquiring Institution ID
        public static final int TRACK2_DATA = 35;           // Track 2 Data
        public static final int RRN = 37;                   // Retrieval Reference Number
        public static final int AUTH_CODE = 38;             // Authorization Code
        public static final int RESPONSE_CODE = 39;         // Response Code
        public static final int TERMINAL_ID = 41;           // Terminal ID
        public static final int MERCHANT_ID = 42;           // Merchant ID
        public static final int ADDITIONAL_DATA = 48;       // Additional Data
        public static final int CURRENCY_CODE = 49;         // Transaction Currency Code
        public static final int PIN_DATA = 52;              // PIN Data
        public static final int ADDITIONAL_AMOUNTS = 54;    // Additional Amounts
        public static final int ORIGINAL_DATA = 90;         // Original Data Elements
        public static final int REPLACEMENT_AMOUNTS = 95;   // Replacement Amounts
    }

    /**
     * Response Codes
     */
    public static class ResponseCode {
        public static final String APPROVED = "00";
        public static final String INSUFFICIENT_FUNDS = "51";
        public static final String INVALID_CARD = "14";
        public static final String INVALID_PIN = "55";
        public static final String CARD_EXPIRED = "33";
        public static final String CARD_BLOCKED = "43";
        public static final String SYSTEM_ERROR = "96";
        public static final String TIMEOUT = "68";
    }
}
