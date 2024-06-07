package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
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
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

    public User() {}

    public User(RoleEnum role, String username, String password, String email) {
        this.role = role;
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
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return role != RoleEnum.ROLE_EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return role != RoleEnum.ROLE_LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return role != RoleEnum.ROLE_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return role != RoleEnum.ROLE_LOCKED && role != RoleEnum.ROLE_EXPIRED;
    }
}
