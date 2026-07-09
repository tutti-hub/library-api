package com.jjtutti.library.dao.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jjtutti.library.dao.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("""
            select u from User u 
            left join fetch u.roles 
            where u.username = :username 
            """)
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
}
