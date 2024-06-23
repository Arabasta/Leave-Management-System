package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    @Query("Select m from Manager as m where m.name like CONCAT('%', :k, '%') ")
    List<Employee> findManagerByName(@Param("k") String keyword);

    @Query("Select m from Manager as m where m.id != :k")
    List<Manager> findAllExcept(@Param("k") Integer employeeId);
}
