package com.jjtutti.library.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.jupiter.api.Test;
import com.jjtutti.library.dao.entity.user.User;
import static com.jjtutti.library.auth.RefreshTokenTest.RefreshTokenTestHelpers.*;

public class RefreshTokenTest {

        @Test
        public void shouldThrowWhenTokenHashNullOrBlank() {
                String tokenHashNull = null;
                String mesasage = "Token hash can not be null or empty";

                assertThatThrownBy(() -> tokenExpiringAt(tokenHashNull, new Date()))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage(mesasage);

                String tokenHashBlank = " ";
                assertThatThrownBy(() -> tokenExpiringAt(tokenHashBlank, new Date()))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage(mesasage);
        }

        @Test
        public void shouldThrowWhenUserNull() {
                assertThatThrownBy(() -> RefreshToken.builder().tokenHash("Q2W1E3").build())
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("User can not be null");
        }

        @Test
        public void shouldThrowWhenCreatedAtNull() {

                assertThatThrownBy(() -> RefreshToken.builder()
                                .tokenHash("Q2W1E3")
                                .user(new User("u1", "pass"))
                                .build())
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Created at can not be null");
        }

        @Test
        public void shouldThrowWhenExpiresAtNull() {
                assertThatThrownBy(() -> tokenExpiringAt(null))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Expires at can not be null");
        }

        @Test
        public void shouldBeExpired() {
                Date expiresAt = Date.from(Instant.now()
                                .minus(1, ChronoUnit.HOURS));
                RefreshToken refreshToken = tokenExpiringAt(expiresAt);
                assertThat(refreshToken.isExpired()).isTrue();
        }

        @Test
        public void shouldNotBeExpired() {
                Date expiresAt = Date.from(Instant.now()
                                .plus(1, ChronoUnit.HOURS));
                RefreshToken refreshToken = tokenExpiringAt(expiresAt);
                assertThat(refreshToken.isExpired()).isFalse();
        }

        @Test
        public void shouldBeNotActiveIfExpired() {
                Date expiresAt = Date.from(Instant.now().minus(1, ChronoUnit.HOURS));
                RefreshToken refreshToken = tokenExpiringAt(expiresAt);
                assertThat(refreshToken.isExpired()).isTrue();
                assertThat(refreshToken.isActive()).isFalse();
        }

        @Test
        public void shouldBeNotActiveIfExpiredAndRevoked() {
                Date expiresAt = Date.from(Instant.now().minus(1, ChronoUnit.HOURS));
                RefreshToken refreshToken = tokenExpiringAt(expiresAt);
                refreshToken.revoke();
                assertThat(refreshToken.isExpired()).isTrue();
                assertThat(refreshToken.isRevoked()).isTrue();
                assertThat(refreshToken.isActive()).isFalse();
        }

        @Test
        public void shouldBeNotActiveIfNotExpiredAndRevoked() {
                Date expiresAt = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
                RefreshToken refreshToken = tokenExpiringAt(expiresAt);
                refreshToken.revoke();
                assertThat(refreshToken.isExpired()).isFalse();
                assertThat(refreshToken.isRevoked()).isTrue();
                assertThat(refreshToken.isActive()).isFalse();
        }

        @Test
        public void shouldBeActiveIfNotExpiredAndNotRevoked() {
                Date expiresAt = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
                RefreshToken refreshToken = tokenExpiringAt(expiresAt);

                assertThat(refreshToken.isExpired()).isFalse();
                assertThat(refreshToken.isRevoked()).isFalse();
                assertThat(refreshToken.isActive()).isTrue();
        }

        @Test
        public void hashCodeShouldBeZeroIfIdIsNull() {
                RefreshToken refreshToken = tokenExpiringAt(new Date());
                assertThat(refreshToken.hashCode()).isEqualTo(0);
        }

        @Test
        public void shouldBeEqualToItself() {
                RefreshToken refreshToken = tokenExpiringAt(new Date());
                assertThat(refreshToken).isEqualTo(refreshToken);
        }

        static class RefreshTokenTestHelpers {

                public static RefreshToken tokenExpiringAt(String tokenHash, User user, Date expiresAt) {
                        return new RefreshToken(
                                        tokenHash,
                                        user,
                                        new Date(),
                                        expiresAt);
                }

                public static RefreshToken tokenExpiringAt(String tokenHash, Date expiresAt) {
                        return tokenExpiringAt(tokenHash, new User("u1", "pass"), expiresAt);
                }

                public static RefreshToken tokenExpiringAt(Date expiresAt) {
                        return tokenExpiringAt("Q3W2E1", new User("u1", "pass"), expiresAt);
                }

        }

}
