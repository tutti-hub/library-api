package com.jjtutti.library.auth;

import java.util.Date;
import java.util.Objects;
import com.jjtutti.library.dao.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = "user")
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private Date expiresAt; 

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false, updatable = false)
    private Date createdAt;

    protected RefreshToken(){}

    @Builder
    public RefreshToken(String tokenHash, User user, Date createdAt, Date expiresAt) {
        
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("Token hash can not be null or empty");
        }

        if (user == null) {
            throw new IllegalArgumentException("User can not be null");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("Created at can not be null");
        }

        if (expiresAt == null ) {
            throw new IllegalArgumentException("Expires at can not be null");
        }
        
        this.tokenHash = tokenHash;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        
    }

    public void revoke() {
        revoked = true;
    }

    public boolean isExpired() {
        return expiresAt.before(new Date());
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }

    @Override
    public int hashCode() {
        return  id == null ? 0 : id.hashCode();
    } 

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (id == null || obj == null || getClass() != obj.getClass()) return false;
      
        RefreshToken other = (RefreshToken)obj;
        
        return Objects.equals(id, other.id);
    }
    
}
