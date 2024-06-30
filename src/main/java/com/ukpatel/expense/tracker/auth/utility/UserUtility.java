package com.ukpatel.expense.tracker.auth.utility;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class UserUtility {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private UserUtility() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static String generateAuthCode(int length) {
        long lowerBound = (long) Math.pow(10, length - 1);
        long upperBound = (long) Math.pow(10, length) - 1;
        return String.valueOf(secureRandom.nextLong(lowerBound, upperBound));
    }

    public static String randomSecureString(int length) {
        return IntStream.range(0, length)
                .boxed()
                .map(index -> String.valueOf(ALPHANUMERIC.charAt(secureRandom.nextInt(ALPHANUMERIC.length()))))
                .collect(Collectors.joining(""));
    }
}
