package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
