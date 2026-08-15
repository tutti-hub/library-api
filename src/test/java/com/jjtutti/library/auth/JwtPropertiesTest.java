package com.jjtutti.library.auth;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jjtutti.library.auth.infrastructure.JwtProperties;

@SpringBootTest
class JwtPropertiesTest {

    @Autowired
    JwtProperties properties;

    @Test
    void shouldBindProperties() {
        assertThat(properties.secret()).isNotBlank();
        assertThat(properties.expiration()).isNotNull();
        assertThat(properties.refreshExpiration()).isNotNull();
    }
}
