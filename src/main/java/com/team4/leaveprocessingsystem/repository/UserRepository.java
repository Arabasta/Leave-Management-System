package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
}
