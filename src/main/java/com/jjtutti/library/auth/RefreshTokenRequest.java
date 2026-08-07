package com.jjtutti.library.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    
    @NotBlank
    String refreshToken) {}
