package com.jjtutti.library.auth.application.dto;

import com.jjtutti.library.auth.dao.RefreshToken;

public record RefreshTokenPair(
    String token, 
    RefreshToken refreshToken) {}
