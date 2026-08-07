package com.jjtutti.library.auth;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record  JwtProperties(String secret, Duration expiration, Duration refreshExpiration) {
    
}
