package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.AccessLevelEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Setter
@Getter
@Entity
@Table(name = "users") // cause 'user' reserved in sql
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private AccessLevelEnum accessLevel;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

    public User() {}

    public User(AccessLevelEnum accessLevel, String username, String password, String email) {
        this.accessLevel = accessLevel;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @PrePersist
    protected void onCreate() {
        createDateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDateTime = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(accessLevel.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return accessLevel != AccessLevelEnum.ROLE_EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accessLevel != AccessLevelEnum.ROLE_LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accessLevel != AccessLevelEnum.ROLE_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return accessLevel != AccessLevelEnum.ROLE_LOCKED && accessLevel != AccessLevelEnum.ROLE_EXPIRED;
    }
}
