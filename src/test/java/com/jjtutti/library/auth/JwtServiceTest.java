package com.jjtutti.library.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import static org.assertj.core.api.Assertions.*;
import java.time.Duration;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

public class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            new JwtProperties(
                    "12345678901234567890123456789012",
                    Duration.ofMinutes(1)));

    @Test
    public void shouldGenerateToken() {
        UserDetails userDetails = userDetails("username");
        jwtService.generateToken(userDetails);
    }

    @Test
    public void shouldExtractUsername() {
        String username = "username";
        UserDetails userDetails = userDetails(username);
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void shouldRejectInvalidSignature() {
        UserDetails user = userDetails("user");

        StringBuilder tokenBuilder = new StringBuilder(jwtService.generateToken(user));

        tokenBuilder.deleteCharAt(tokenBuilder.length() - 1).append("A");

        assertThatThrownBy(() -> jwtService.extractUsername(tokenBuilder.toString()))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotMatch() {
        UserDetails user = userDetails("user", "ROLE_USER");
        UserDetails other = userDetails("admin", "ROLE_ADMIN");

        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }

    @Test
    public void shouldReturnTrueForValidToken() {
        UserDetails userDetails = userDetails("username", "ROLE_USER", "ROLE_ADMIN");
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    public void shouldRejectExpiredToken() throws InterruptedException {
        JwtService jwtService = new JwtService(
                new JwtProperties(
                        "12345678901234567890123456789012",
                        Duration.ofMillis(10)));

        UserDetails userDetails = userDetails("username");
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(15);

        assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    private static UserDetails userDetails(String username, String... roles) {
        return User.builder()
                .username(username)
                .authorities(roles)
                .build();
    }
}
