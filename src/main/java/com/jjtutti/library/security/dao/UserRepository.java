package com.jjtutti.library.security.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("""
            select u from User u 
            left join fetch u.roles 
            where u.username = :username 
            """)
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
}
