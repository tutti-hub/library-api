package com.jjtutti.library.auth.dao;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.jjtutti.library.security.dao.User;
import com.jjtutti.library.security.dao.UserRepository;
import static com.jjtutti.library.helpers.RefreshTokenTestHelpers.*;

@ActiveProfiles("test")
@DataJpaTest
public class RefreshTokenIT {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private RefreshToken refreshToken;

    @BeforeEach
    public void beforeEach() {
        user = new User("admin", "pass");
        userRepository.save(user);

        refreshToken = tokenExpiringAt("Q3W2E1", user, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));

        refreshTokenRepository.save(refreshToken);
    }

    @Test
    public void hashCodeShouldMatchIdHashCode() {
        Long id = refreshToken.getId();
        assertThat(id).isNotNull();
        assertThat(refreshToken.hashCode()).isEqualTo(id.hashCode());
    }

    @Test
    public void shouldBeEqualWhenIdsMatch() {
        RefreshToken reloaded = refreshTokenRepository.findAll().getFirst();
        assertThat(refreshToken).isEqualTo(reloaded);
    }

    @Test
    public void shouldAlwaysNotBeEqualsIfIdNull() {
        RefreshToken newRefreshToken = tokenExpiringAt(new Date());
        assertThat(newRefreshToken).isNotEqualTo(refreshToken);
    }

    @Test
    public void shouldNotBeEqualsIfOtherIsNull() {
        assertThat(refreshToken).isNotEqualTo(null);
    }

    @Test
    public void shouldNotBeEqualsIfAnotherClass() {
        assertThat(refreshToken).isNotEqualTo(new Object());
    }

    @Test
    void shouldNotBeEqualsWhenIdsDiffer() {
        RefreshToken another = tokenExpiringAt("Q3W2E1",
                user, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));

        refreshTokenRepository.save(another);

        assertThat(refreshToken).isNotEqualTo(another);
    }
}
