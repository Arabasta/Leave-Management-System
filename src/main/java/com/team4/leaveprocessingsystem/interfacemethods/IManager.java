package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import jakarta.transaction.Transactional;

import java.util.List;

public interface IManager {
    Manager findManagerById(Integer id);
    List<Manager> findAllManagers();
    List<Employee> findManagersByName(String name);

    @Transactional
    List<Manager> findAllExcept(Integer id);
}
