package com.jjtutti.library.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import com.jjtutti.library.infrastructure.common.Sha256HashService;

import jakarta.transaction.Transactional;
import static org.assertj.core.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class AuthenticationServiceIT {

    private static final String username = "root";
    private static final String password = "secret";

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private Sha256HashService hashService;

    @Test
    public void shouldLoginSuccessfully() {

        TokenResponse tokenResponse = authenticationService.login(new LoginRequest(username, password));

        assertThat(jwtService.extractUsername(tokenResponse.accessToken())).isEqualTo(username);
        assertThat(jwtService.extractUsername(tokenResponse.refreshToken())).isEqualTo(username);

        assertThat(refreshTokenRepository.findAll())
                .hasSize(1);
    }

    @Test
    public void shouldThrowBadCredentialsIfWrongUsername() {

        String wrongUsername = "wrongusername";

        assertThatThrownBy(() -> authenticationService.login(new LoginRequest(wrongUsername, password)))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials")
                .hasCause(new UsernameNotFoundException("wrongusername"));

        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    @Test
    public void shouldThrowBadCredentialsExceptionIfWrongPassword() {

        String wrongPassword = "wrongpassword";

        assertThatThrownBy(() -> authenticationService.login(new LoginRequest(username, wrongPassword)))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    @Test
    public void shouldSuccessfullyRefreshToken() {

        TokenResponse tokenResponse = authenticationService.login(new LoginRequest(username, password));

        TokenResponse tokenResponseRefreshed = authenticationService.refreshToken(tokenResponse.refreshToken());

        assertThat(jwtService.extractUsername(tokenResponseRefreshed.accessToken())).isEqualTo(username);

        assertThat(jwtService.extractUsername(tokenResponseRefreshed.refreshToken())).isEqualTo(username);

        assertThat(refreshTokenRepository.count()).isEqualTo(2);

        String oldTokenHash = hashService.hash(tokenResponse.refreshToken());

        RefreshToken oldRefreshToken = refreshTokenRepository.findByTokenHash(oldTokenHash).orElseThrow();

        assertThat(oldRefreshToken.isRevoked()).isTrue();

        String newTokenHash = hashService.hash(tokenResponseRefreshed.refreshToken());

        RefreshToken newRefreshToken = refreshTokenRepository.findByTokenHash(newTokenHash)
                .orElseThrow();

        assertThat(newRefreshToken.isActive()).isTrue();

        assertThat(tokenResponseRefreshed.refreshToken())
                .isNotEqualTo(tokenResponse.refreshToken());
    }

    @Test
    public void shouldThrowRefreshTokenNotFound() {
        TokenResponse tokenResponse = authenticationService.login(new LoginRequest(username, password));

        RefreshToken refreshToken = refreshTokenRepository.findAll().getFirst();

        refreshTokenRepository.delete(refreshToken);

        assertThatThrownBy(() -> authenticationService.refreshToken(
                tokenResponse.refreshToken()))
                .isInstanceOf(RefreshTokenNotFoundException.class)
                .hasMessageContaining(tokenResponse.refreshToken());
    }

    @Test
    public void shouldThrowRefreshTokenNotActive() {
        TokenResponse tokenResponse = authenticationService.login(new LoginRequest(username, password));

        RefreshToken refreshToken = refreshTokenRepository.findAll().getFirst();

        refreshToken.revoke();

        refreshTokenRepository.save(refreshToken);

        assertThatThrownBy(() -> authenticationService.refreshToken(tokenResponse.refreshToken()))
                .isInstanceOf(RefreshTokenNotActiveException.class)
                .hasMessageContaining(refreshToken.getId().toString());
    }

    @Test
    public void shouldRejectReusedRefreshToken() {
        TokenResponse tokenResponse = authenticationService.login(new LoginRequest(username, password));

        authenticationService.refreshToken(tokenResponse.refreshToken());

        assertThatThrownBy(() -> authenticationService.refreshToken(tokenResponse.refreshToken()))
                .isInstanceOf(RefreshTokenNotActiveException.class);
    }
}
