package com.jjtutti.library.security.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static com.jjtutti.library.security.dao.RoleName.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void shouldPersistUser() {
        User user = new User("name", "password");
        Role roleAdmin = new Role(RoleName.ROLE_ADMIN);
        Role roleUser = new Role(RoleName.ROLE_USER);
        
        roleRepository.save(roleAdmin);
        roleRepository.save(roleUser);
        
        user.addRole(roleUser);
        user.addRole(roleAdmin);
        
        userRepository.save(user);
        
        assertThat(user.getId()).isNotNull();
        assertThat(user.getRoles())
            .extracting(Role::getRoleName)
            .containsExactlyInAnyOrder(ROLE_USER, ROLE_ADMIN);
    }

    @Test
    public void shouldReturnUserWhenUsernameExists() {
        String username = "johndoe";
        User user = new User(username, "pass");
        userRepository.save(user);

        var maybeUser = userRepository.findByUsername(username);

        assertThat(maybeUser).isNotEmpty();
        
    }

    @Test 
    public void shouldReturnTrueWhenUsernameExists() {
        String username = "johndoe";
        User user = new User(username, "pass");
        userRepository.save(user);

        boolean userExists = userRepository.existsByUsername(username);
        assertThat(userExists).isTrue();
    }

    
}
