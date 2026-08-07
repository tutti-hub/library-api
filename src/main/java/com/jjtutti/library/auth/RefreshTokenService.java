package com.jjtutti.library.auth;

import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjtutti.library.dao.entity.user.User;
import com.jjtutti.library.infrastructure.common.Sha256HashService;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final Sha256HashService hashService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,  
                            JwtService jwtService, 
                            Sha256HashService hashService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.hashService = hashService;
    }

    @Transactional
    public RefreshTokenPair create(User  user) {
        
        RefreshTokenGenerationResult tokenGenerationResult = jwtService.generateRefreshToken(user.getUsername());
        
        String tokenHash = hashService.hash(tokenGenerationResult.token()); 

        RefreshToken refreshToken = RefreshToken.builder()
            .tokenHash(tokenHash)
            .createdAt(tokenGenerationResult.createdAt())
            .expiresAt(tokenGenerationResult.expiresAt())
            .user(user)
            .build();

        refreshTokenRepository.save(refreshToken);    

        return new RefreshTokenPair(tokenGenerationResult.token(), refreshToken);
    }
 
    public Optional<RefreshToken> findByToken(String token) {
        
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token can not be null or blank");
        }

        String tokenHash = hashService.hash(token);          
        
        return refreshTokenRepository.findByTokenHash(tokenHash);
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {
        Objects.requireNonNull(refreshToken, "Refresh token can not be null");

        var storedRefreshToken = refreshTokenRepository.findById(refreshToken.getId())
        .orElseThrow(() -> new RefreshTokenNotFoundException(refreshToken.getTokenHash()));
        storedRefreshToken.revoke();
    } 

}

interface IRefreshTokenService {
   
    RefreshToken create(User user);

    Optional<RefreshToken> findByToken(String token);

    boolean isValid(RefreshToken refreshToken);

    void revoke(RefreshToken refreshToken);

    void revokeAll(User user);
    

}
