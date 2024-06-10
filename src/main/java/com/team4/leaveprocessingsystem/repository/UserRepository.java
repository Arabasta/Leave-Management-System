package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("Select u from User u join u.employee userEmployee where userEmployee.id = :k")
    List<User> findUserRolesByEmployeeId(@Param("k") Integer employeeId);

}
