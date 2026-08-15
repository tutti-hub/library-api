package com.jjtutti.library.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.jjtutti.library.auth.application.JwtService;
import com.jjtutti.library.auth.infrastructure.JwtProperties;
import com.jjtutti.library.security.dao.Role;
import com.jjtutti.library.security.dao.User;

import static com.jjtutti.library.security.dao.RoleName.*;
import static org.assertj.core.api.Assertions.*;
import java.time.Duration;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

public class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            new JwtProperties(
                    "12345678901234567890123456789012",
                    Duration.ofMinutes(1), Duration.ofHours(1)));

    @Test
    public void shouldGenerateTokenByUserDetails() {
        UserDetails userDetails = userDetails("username");
        jwtService.generateToken(userDetails);
    }

    @Test
    public void shouldGenerateValidTokenForUser() {
        String username = "admin";
        User user = new User(username, "password");
        user.addRole(new Role(ROLE_ADMIN));
        String token = jwtService.generateToken(user);

        jwtService.validateToken(token);
      
        assertThat(jwtService.extractUsername(token)).isEqualTo(username);
    }

    @Test
    public void shouldGenerateRefreshToken() {
        String username = "admin";
        var result = jwtService.generateRefreshToken(username);
        String token = result.token();
        jwtService.validateToken(token);
        assertThat(jwtService.extractUsername(token)).isEqualTo(username);
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.expiresAt()).isNotNull();
        assertThat(result.expiresAt()).isAfter(result.createdAt());
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
                        Duration.ofMillis(10), Duration.ofHours(1)));

        UserDetails userDetails = userDetails("username");
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(15);

        assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    private static UserDetails userDetails(String username, String... roles) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .authorities(roles)
                .build();
    }
}
