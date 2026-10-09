package com.jjtutti.library.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jjtutti.library.security.dao.Role;
import com.jjtutti.library.security.dao.RoleName;
import com.jjtutti.library.security.dao.RoleRepository;
import com.jjtutti.library.security.dao.User;
import com.jjtutti.library.security.dao.UserRepository;
import com.jjtutti.library.security.infrastructure.exceptions.RoleNotFoundException;

@Configuration
public class DatabaseInitializer {

    @Bean
    @Transactional
    public CommandLineRunner init(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            @Value("${APP_ROOT_USERNAME}") String appRootUsername,
            @Value("${APP_ROOT_PASSWORD}") String appRootPassword) {

        return args -> {

            for (var roleName : RoleName.values()) {
                if (!roleRepository.existsByRoleName(roleName)) {
                    roleRepository.save(new Role(roleName));
                }
            }

            if (!userRepository.existsByUsername(appRootUsername)) {

                User user = new User(appRootUsername, passwordEncoder.encode(appRootPassword));

                Role roleAdmin = roleRepository
                        .findByRoleName(RoleName.ROLE_ADMIN)
                        .orElseThrow(() -> new RoleNotFoundException(RoleName.ROLE_ADMIN));

                user.addRole(roleAdmin);

                userRepository.save(user);
            }
        };
    }
}
