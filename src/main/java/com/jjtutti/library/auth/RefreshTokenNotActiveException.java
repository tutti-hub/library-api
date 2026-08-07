package com.jjtutti.library.auth;

public class RefreshTokenNotActiveException extends RuntimeException {
    public RefreshTokenNotActiveException(Long tokenId) {
        super("Token with id " + tokenId + " is not active");
    }
}
