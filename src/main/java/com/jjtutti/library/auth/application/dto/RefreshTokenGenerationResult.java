package com.jjtutti.library.auth.application.dto;

import java.util.Date;

public record RefreshTokenGenerationResult(String token, Date createdAt, Date expiresAt) {

}
