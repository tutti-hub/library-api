package com.jjtutti.library.auth;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.jjtutti.library.dao.entity.user.User;
import com.jjtutti.library.dao.repository.user.UserRepository;
import com.jjtutti.library.infrastructure.common.Sha256HashService;

@ActiveProfiles("test")
@DataJpaTest
public class RefreshTokenServiceIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private UserRepository userRepository;

    private JwtService jwtService = new JwtService(
            new JwtProperties(
                    "12345678901234567890123456789012",
                    Duration.ofMinutes(1), Duration.ofHours(1)));
    
    private Sha256HashService hashService = new Sha256HashService();

    private RefreshTokenService refreshTokenService;
    
    @BeforeEach
    public void beforeEach() {
         refreshTokenService = new RefreshTokenService(refreshTokenRepository, jwtService, hashService);
    }
   

    @Test
    public void shouldRevokeSuccessfull() {

        User user = userRepository.save(new User("username", "password"));
        RefreshTokenPair refreshTokenPair = refreshTokenService.create(user);

        RefreshToken refreshToken = refreshTokenPair.refreshToken();
        refreshTokenService.revoke(refreshToken);
        
        entityManager.flush();
        entityManager.clear();

        RefreshToken reloaded = refreshTokenRepository.findById(refreshToken.getId()).orElseThrow();

        assertThat(reloaded.isRevoked()).isTrue();
    }
}
