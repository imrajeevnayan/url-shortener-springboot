package com.urlshortener.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Base62 Encoder for generating short codes
 * Uses alphanumeric characters (0-9, a-z, A-Z)
 */
@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;
    private static final int SHORT_CODE_LENGTH = 7;
    private final Random random = new SecureRandom();

    /**
     * Encode a number to Base62 string
     */
    public String encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.insert(0, BASE62_CHARS.charAt((int) (number % BASE)));
            number /= BASE;
        }
        // Pad with random characters if too short
        while (sb.length() < SHORT_CODE_LENGTH) {
            sb.insert(0, BASE62_CHARS.charAt(random.nextInt(BASE)));
        }
        return sb.toString();
    }

    /**
     * Generate a random short code
     */
    public String generateRandomCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(BASE62_CHARS.charAt(random.nextInt(BASE)));
        }
        return sb.toString();
    }

    /**
     * Decode Base62 string to number
     */
    public long decode(String code) {
        long result = 0;
        for (int i = 0; i < code.length(); i++) {
            result = result * BASE + BASE62_CHARS.indexOf(code.charAt(i));
        }
        return result;
    }
}
