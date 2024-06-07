package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;

import java.util.List;

public interface IEmployee {
    boolean save(Employee employee);
    Manager findManagerByName(String name);
    List<Employee> findEmployeesByManager(Manager manager);
    Employee findByName(String name);
    long count();
}
