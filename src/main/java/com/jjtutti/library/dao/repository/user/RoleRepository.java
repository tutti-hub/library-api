package com.jjtutti.library.dao.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jjtutti.library.dao.entity.user.Role;
import com.jjtutti.library.dao.entity.user.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleName(RoleName roleName);    
}
