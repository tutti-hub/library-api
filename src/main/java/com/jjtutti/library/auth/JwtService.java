package com.jjtutti.library.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.jjtutti.library.dao.entity.user.User;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails.getUsername(),
                userDetails
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());
    }

    public String generateToken(User user) {
        return buildToken(user.getUsername(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getRoleName().name())
                        .toList());
    }

    private String buildToken(String username, Collection<? extends String> authorities) {
        Instant now = Instant.now();

        return jwtBuilder(username,
                Date.from(now),
                Date.from(now.plus(jwtProperties.expiration())))
                .id(UUID.randomUUID().toString())
                .claim("authorities", authorities)
                .compact();
    }

    public RefreshTokenGenerationResult generateRefreshToken(String username) {
        Instant now = Instant.now();
        Date createdAt = Date.from(now);
        Date expiresAt = Date.from(now.plus(jwtProperties.refreshExpiration()));

        String token = jwtBuilder(username,
                createdAt,
                expiresAt)
                .id(UUID.randomUUID().toString())
                .compact();

        return new RefreshTokenGenerationResult(token, createdAt, expiresAt);
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername());
    }

    public void validateToken(String token) {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    private JwtBuilder jwtBuilder(String username, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey());
    }

}
