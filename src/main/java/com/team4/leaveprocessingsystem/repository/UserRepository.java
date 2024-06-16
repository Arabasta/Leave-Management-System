package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.employee.id = :employeeId")
    List<User> findByEmployeeId(Integer employeeId);

    User findById(int id);

}
