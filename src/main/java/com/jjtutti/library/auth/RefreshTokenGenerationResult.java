package com.jjtutti.library.auth;

import java.util.Date;

public record RefreshTokenGenerationResult(String token, Date createdAt, Date expiresAt) {

}
