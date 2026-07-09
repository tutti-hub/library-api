package com.jjtutti.library.dao.repository.user;

import static com.jjtutti.library.dao.entity.user.RoleName.ROLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.jjtutti.library.dao.entity.user.Role;

@ActiveProfiles("test")
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void shouldReturnTrueWhenRoleNameExists() {
        Role role = new Role(ROLE_ADMIN);
        roleRepository.save(role);
        assertThat(roleRepository.existsByRoleName(ROLE_ADMIN)).isTrue();
    }

    @Test 
    public void shouldReturnFalseWhenRoleNameDoesNotExist() {
        assertThat(roleRepository.existsByRoleName(ROLE_ADMIN)).isFalse();
    }
}
