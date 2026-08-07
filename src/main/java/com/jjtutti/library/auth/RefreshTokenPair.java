package com.jjtutti.library.auth;

public record RefreshTokenPair(
    String token, 
    RefreshToken refreshToken) {}
