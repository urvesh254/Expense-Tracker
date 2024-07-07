package com.ukpatel.expense.tracker.auth.jwt.constant;

public final class JwtConstants {

    public static final String TOKEN_TYPE = "tokenType";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_PREFIX = "Bearer ";

    // Prevent instantiation
    private JwtConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

}
