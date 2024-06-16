package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class EmployeeService implements IEmployee {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public boolean save(Employee employee) {
        try {
            employeeRepository.save(employee);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException("Failed to save employee: " + employee.getName(), e);
        }
    }

    @Override
    @Transactional
    public long count() {
        return employeeRepository.count();
    }

    @Override
    @Transactional
    public Manager findManagerByName(String name) {
        return (Manager) employeeRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Manager " + name + " not found"));
    }

    @Override
    @Transactional
    public List<Employee> findEmployeesByManager(Manager manager) {
        return employeeRepository.findByManager(manager);
    }

    @Override
    @Transactional
    public Employee findByName(String name) {
        return employeeRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Employee " + name + " not found"));
    }

    @Override
    @Transactional
    public List<Employee> findEmployeesByName(String name) {
        return employeeRepository.findEmployeesByName(name);
    }

    @Override
    @Transactional
    public List<Employee> findEmployeesByJobDesignation(String jobDesignation) {
        return employeeRepository.findEmployeesByJobDesignation(jobDesignation);
    }

    @Override
    @Transactional
    public List<Employee> findUsersByRoleType(String roleType) {
        return employeeRepository.findUsersByRoleType(roleType);
    }

    @Override
    @Transactional
    public Employee findEmployeeById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee " + id + " not found"));
    }

    @Override
    @Transactional
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional
    public void removeEmployee(Employee employee) {
        employeeRepository.delete(employee);
    }
}
