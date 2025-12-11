package com.banking.iso8583;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * ISO8583 Message Parser
 * Parses and formats ISO8583 messages
 */
@Component
public class ISO8583Parser {

    /**
     * Parse ISO8583 message from byte array
     */
    public ISO8583Message parse(byte[] data) throws Exception {
        String message = new String(data, StandardCharsets.UTF_8);
        return parse(message);
    }

    /**
     * Parse ISO8583 message from string
     */
    public ISO8583Message parse(String message) throws Exception {
        if (message == null || message.length() < 20) {
            throw new IllegalArgumentException("Invalid ISO8583 message");
        }

        ISO8583Message iso8583Message = new ISO8583Message();

        int offset = 0;

        // Extract MTI (4 characters)
        String mti = message.substring(offset, offset + 4);
        iso8583Message.setMti(mti);
        offset += 4;

        // Extract Primary Bitmap (16 hex characters = 64 bits)
        String primaryBitmap = message.substring(offset, offset + 16);
        offset += 16;

        BitSet bitmap = hexToBitSet(primaryBitmap);

        // Check if secondary bitmap is present
        boolean hasSecondaryBitmap = bitmap.get(0);
        if (hasSecondaryBitmap) {
            String secondaryBitmap = message.substring(offset, offset + 16);
            offset += 16;
            BitSet secondaryBitSet = hexToBitSet(secondaryBitmap);
            // Merge bitmaps
            for (int i = 0; i < 64; i++) {
                bitmap.set(64 + i, secondaryBitSet.get(i));
            }
        }

        // Extract data elements based on bitmap
        offset = extractDataElements(message, offset, bitmap, iso8583Message);

        return iso8583Message;
    }

    /**
     * Format ISO8583 message to string
     */
    public String format(ISO8583Message message) {
        StringBuilder result = new StringBuilder();

        // Add MTI
        result.append(message.getMti());

        // Build bitmap
        BitSet bitmap = buildBitmap(message);

        // Add primary bitmap
        result.append(bitSetToHex(bitmap, 0, 64));

        // Add secondary bitmap if needed
        if (hasFieldsAbove64(message)) {
            result.append(bitSetToHex(bitmap, 64, 128));
        }

        // Add data elements
        for (int i = 2; i <= 128; i++) {
            if (message.hasField(i)) {
                String value = message.getField(i);
                result.append(formatField(i, value));
            }
        }

        return result.toString();
    }

    /**
     * Extract data elements from message
     */
    private int extractDataElements(String message, int offset, BitSet bitmap, ISO8583Message iso8583Message) {
        for (int i = 1; i < (bitmap.length() > 64 ? 128 : 64); i++) {
            if (bitmap.get(i)) {
                FieldDefinition fieldDef = getFieldDefinition(i + 1);
                if (fieldDef != null && offset < message.length()) {
                    String value;
                    if (fieldDef.isVariable()) {
                        int lengthDigits = fieldDef.getLengthDigits();
                        int fieldLength = Integer.parseInt(message.substring(offset, offset + lengthDigits));
                        offset += lengthDigits;
                        value = message.substring(offset, Math.min(offset + fieldLength, message.length()));
                        offset += fieldLength;
                    } else {
                        int fieldLength = fieldDef.getLength();
                        value = message.substring(offset, Math.min(offset + fieldLength, message.length()));
                        offset += fieldLength;
                    }
                    iso8583Message.setField(i + 1, value);
                }
            }
        }
        return offset;
    }

    /**
     * Build bitmap from message fields
     */
    private BitSet buildBitmap(ISO8583Message message) {
        BitSet bitmap = new BitSet(128);

        for (int field : message.getDataElements().keySet()) {
            if (field > 1 && field <= 128) {
                bitmap.set(field - 1);
            }
        }

        // Set bit 1 if fields > 64 are present
        if (hasFieldsAbove64(message)) {
            bitmap.set(0);
        }

        return bitmap;
    }

    /**
     * Check if message has fields above 64
     */
    private boolean hasFieldsAbove64(ISO8583Message message) {
        return message.getDataElements().keySet().stream().anyMatch(f -> f > 64);
    }

    /**
     * Convert hex string to BitSet
     */
    private BitSet hexToBitSet(String hex) {
        BitSet bits = new BitSet(hex.length() * 4);
        for (int i = 0; i < hex.length(); i++) {
            int value = Integer.parseInt(hex.substring(i, i + 1), 16);
            for (int j = 0; j < 4; j++) {
                if ((value & (1 << (3 - j))) != 0) {
                    bits.set(i * 4 + j);
                }
            }
        }
        return bits;
    }

    /**
     * Convert BitSet to hex string
     */
    private String bitSetToHex(BitSet bits, int start, int end) {
        StringBuilder hex = new StringBuilder();
        for (int i = start; i < end; i += 4) {
            int value = 0;
            for (int j = 0; j < 4 && (i + j) < end; j++) {
                if (bits.get(i + j)) {
                    value |= (1 << (3 - j));
                }
            }
            hex.append(Integer.toHexString(value).toUpperCase());
        }
        return hex.toString();
    }

    /**
     * Format individual field
     */
    private String formatField(int field, String value) {
        FieldDefinition fieldDef = getFieldDefinition(field);
        if (fieldDef == null) {
            return value;
        }

        if (fieldDef.isVariable()) {
            int lengthDigits = fieldDef.getLengthDigits();
            String length = String.format("%0" + lengthDigits + "d", value.length());
            return length + value;
        }

        return value;
    }

    /**
     * Get field definition (simplified version)
     */
    private FieldDefinition getFieldDefinition(int field) {
        // This is a simplified version. In production, use a complete field definition table
        return switch (field) {
            case 2 -> new FieldDefinition(19, true, 2);  // PAN - LLVAR
            case 3 -> new FieldDefinition(6, false, 0);   // Processing Code
            case 4 -> new FieldDefinition(12, false, 0);  // Amount
            case 11 -> new FieldDefinition(6, false, 0);  // STAN
            case 12 -> new FieldDefinition(6, false, 0);  // Time
            case 13 -> new FieldDefinition(4, false, 0);  // Date
            case 37 -> new FieldDefinition(12, false, 0); // RRN
            case 38 -> new FieldDefinition(6, false, 0);  // Auth Code
            case 39 -> new FieldDefinition(2, false, 0);  // Response Code
            case 41 -> new FieldDefinition(8, false, 0);  // Terminal ID
            case 48 -> new FieldDefinition(999, true, 3); // Additional Data - LLLVAR
            default -> new FieldDefinition(0, false, 0);
        };
    }

    /**
     * Field Definition helper class
     */
    private static class FieldDefinition {
        private final int length;
        private final boolean variable;
        private final int lengthDigits;

        public FieldDefinition(int length, boolean variable, int lengthDigits) {
            this.length = length;
            this.variable = variable;
            this.lengthDigits = lengthDigits;
        }

        public int getLength() { return length; }
        public boolean isVariable() { return variable; }
        public int getLengthDigits() { return lengthDigits; }
    }
}
