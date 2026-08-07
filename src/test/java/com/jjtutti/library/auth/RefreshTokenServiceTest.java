package com.jjtutti.library.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.jjtutti.library.dao.entity.user.User;
import com.jjtutti.library.infrastructure.common.Sha256HashService;

public class RefreshTokenServiceTest {

    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);

    private final JwtService jwtService = mock(JwtService.class);

    private final Sha256HashService hashService = mock(Sha256HashService.class);

    private final RefreshTokenService refreshTokenService = new RefreshTokenService(refreshTokenRepository, jwtService,
            hashService);

    @Test
    public void shouldThrowIllegalArgumentWhenTokenNullOrBlank() {
        assertThatThrownBy(() -> refreshTokenService.findByToken(null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("null or blank");
        assertThatThrownBy(() -> refreshTokenService.findByToken(" "))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("null or blank");
    }

    @Test
    public void shouldFindRefreshToken() {
        String token = "Hello World";
        String tokenHash = "Q1W2E3R4T5Y6";
        Instant now = Instant.now();
        Date createdAt = Date.from(now);
        Date expiresAt = Date.from(now.plus(1, ChronoUnit.HOURS));

        when(hashService.hash(token)).thenReturn(tokenHash);

        when(refreshTokenRepository.findByTokenHash(tokenHash))
                .thenReturn(Optional.of(
                        new RefreshToken(tokenHash, new User("username", "password"), createdAt, expiresAt)));

        RefreshToken refreshToken = refreshTokenService.findByToken(token).get();
        assertThat(refreshToken.getCreatedAt()).isEqualTo(createdAt);
        assertThat(refreshToken.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(refreshToken.getUser().getUsername()).isEqualTo("username");
    }

    @Test
    public void shouldThrowNpeWhenRevokingNullToken() {
        assertThatThrownBy(() -> refreshTokenService.revoke(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Refresh token can not be null");
    }

    @Test
    public void shouldThrowRefreshTokenNotFoundWhenrevokeNotExistingToken() {
        String tokenHash = "Q1W2E3R4T5Y6";
        Long id = 101L;

        RefreshToken refreshToken = mock(RefreshToken.class);

        when(refreshToken.getId()).thenReturn(id);
        when(refreshToken.getTokenHash()).thenReturn(tokenHash);

        when(refreshTokenRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.revoke(refreshToken))
                .isInstanceOf(RefreshTokenNotFoundException.class)
                .hasMessageContaining(tokenHash);

        verify(refreshTokenRepository).findById(id);
        verify(refreshTokenRepository, never()).save(any());
    }

}
