package com.jjtutti.library.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jjtutti.library.security.dao.Role;
import com.jjtutti.library.security.dao.RoleName;
import com.jjtutti.library.security.dao.RoleRepository;
import com.jjtutti.library.security.dao.User;
import com.jjtutti.library.security.dao.UserRepository;

@Configuration
public class DatabaseInitializer {
    
    @Bean
    public CommandLineRunner init(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        return  args -> {
            String username = "root";
            String password = "secret";

            if (!userRepository.existsByUsername(username)) {

                User user = new User(username, passwordEncoder.encode(password));

                for (var roleName : RoleName.values()) {
                    if (!roleRepository.existsByRoleName(roleName)) {
                        Role role = roleRepository.save(new Role(roleName));
                        user.addRole(role);
                    }
                }
                userRepository.save(user);
            }    
        };
    }
}
