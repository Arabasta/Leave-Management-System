package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByName(String name);
    List<Employee> findByManager(Manager manager);
}
