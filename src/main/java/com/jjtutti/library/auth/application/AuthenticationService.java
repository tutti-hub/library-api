package com.jjtutti.library.auth.application;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jjtutti.library.auth.application.dto.LoginRequest;
import com.jjtutti.library.auth.application.dto.RefreshTokenPair;
import com.jjtutti.library.auth.application.dto.TokenResponse;
import com.jjtutti.library.auth.dao.RefreshToken;
import com.jjtutti.library.auth.infrastructure.exceptions.RefreshTokenNotActiveException;
import com.jjtutti.library.auth.infrastructure.exceptions.RefreshTokenNotFoundException;
import com.jjtutti.library.security.domain.model.CustomUserDetails;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(JwtService jwtService,
            RefreshTokenService refreshTokenService,
            AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;

    }

    public TokenResponse login(LoginRequest loginRequest) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.username(),
                loginRequest.password());

        Authentication authenticated = authenticationManager.authenticate(authentication);

        CustomUserDetails principal = (CustomUserDetails) authenticated.getPrincipal();

        String accessToken = jwtService.generateToken(principal);

        RefreshTokenPair refreshTokenPair = refreshTokenService.create(principal.getUser());

        return new TokenResponse(accessToken, refreshTokenPair.token());
    }

    @Transactional
    public TokenResponse refreshToken(String token) {
        
        RefreshToken refreshToken = validateAndRevoke(token);

        var currentUser = refreshToken.getUser();

        // Создать новый refresh
        RefreshTokenPair refreshTokenPair = 
            refreshTokenService.create(currentUser);
        
        // Создать новый access
        String accessToken = jwtService.generateToken(currentUser);
        
        // Вернуть новую пару клиенту     
        return new TokenResponse(accessToken, refreshTokenPair.token());
        
    }

    @Transactional
    public void logout(String token) {
        validateAndRevoke(token);
    }

    private RefreshToken validateAndRevoke(String token) {
        // Проверить подпись и срок действия JWT
        jwtService.verifyAndParseClaims(token);

        // Вычислить hash
        // Найти RefreshToken в базе
        RefreshToken refreshToken = refreshTokenService.findByToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException(token));
        
        // Проверить, что запись активна (!revoked)
        if (!refreshToken.isActive()) {
            throw new RefreshTokenNotActiveException(refreshToken.getId()); 
        }

        // Отозвать старый refresh
        refreshTokenService.revoke(refreshToken);

        return refreshToken;
    }
}
