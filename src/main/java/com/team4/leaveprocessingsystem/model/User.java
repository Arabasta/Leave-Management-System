package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;
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
@SQLDelete(sql = "update users set deleted = true where id=?") // changes deleted field into true rather than deleting data permanently
@Where(clause = "deleted=false") // read only employees where deleted=false
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Length(min=3, max=20, message = "Username must be 3-20 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @Length(min=6, message = "Password must at least 6 characters")
    @Column(nullable = false)
    private String password;

    @Email(message = "Please input a valid Email")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false)
    private String email;

    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

    private boolean deleted = Boolean.FALSE;

    public User() {}

    public User(RoleEnum role, String username, String password, String email, Employee employee) {
        this.role = role;
        this.username = username;
        this.password = password;
        this.email = email;
        this.employee = employee;
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
