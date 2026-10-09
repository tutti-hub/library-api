package com.jjtutti.library.security.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleName(RoleName roleName);    
    Optional<Role> findByRoleName(RoleName roleName);
}
