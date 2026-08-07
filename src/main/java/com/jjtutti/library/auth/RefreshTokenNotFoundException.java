package com.jjtutti.library.auth;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String token) {
        super("Refresh token '" + token + "' not found");
    }
}
