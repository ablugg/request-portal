package com.github.ablugg.requestportal.request;

import java.security.SecureRandom;

/**
 * Makes the public tracking code a customer uses to look up their request, e.g. SR-7K3M9Q2A.
 *
 * The code is random rather than a counter (SR-1, SR-2, ...) so that nobody can view other
 * people's requests by guessing the next number. Look-alike characters (0/O, 1/I/L) are
 * left out so the code is easy to read back over the phone.
 */
final class ReferenceGenerator {

    static final String PREFIX = "SR-";
    static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    static final int LENGTH = 8;

    private static final SecureRandom RANDOM = new SecureRandom();

    private ReferenceGenerator() {
    }

    static String next() {
        StringBuilder code = new StringBuilder(PREFIX);
        for (int i = 0; i < LENGTH; i++) {
            code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }
}
