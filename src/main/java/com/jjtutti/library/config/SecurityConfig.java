package com.jjtutti.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
import static com.jjtutti.library.dao.entity.user.RoleName.*;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/books/**")
                        .hasAnyAuthority(
                                ROLE_USER.name(),
                                ROLE_LIBRARIAN.name(),
                                ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.POST, "/api/books/**")
                        .hasAnyAuthority(
                                ROLE_LIBRARIAN.name(),
                                ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.PUT, "/api/books/**")
                        .hasAnyAuthority(
                                ROLE_LIBRARIAN.name(),
                                ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.DELETE, "/api/books/**")
                        .hasAuthority(ROLE_ADMIN.name())

                        .anyRequest().authenticated())
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
