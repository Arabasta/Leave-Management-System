package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import jakarta.transaction.Transactional;

import java.util.List;

public interface IEmployee {
    boolean save(Employee employee);
    Manager findManagerByName(String name);
    List<Employee> findEmployeesByManager(Manager manager);
    Employee findByName(String name);
    long count();

    List<Employee> findAll();

    List<Employee> findEmployeesByName(String keyword);

    List<Employee> findEmployeesByJobDesignation(String jobDesignation);

    List<Employee> findUsersByRoleType(String roleType);
    Employee findEmployeeById(int id);

    @Transactional
    void removeEmployee(Employee employee);
}
