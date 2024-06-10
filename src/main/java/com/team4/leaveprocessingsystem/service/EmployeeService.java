package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EmployeeService implements IEmployee {
    @Autowired
    private final EmployeeRepository employeeRepository;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public EmployeeService(EmployeeRepository employeeRepository, LeaveBalanceRepository leaveBalanceRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public boolean save(Employee employee) {
        try {
            // add some logic here to auto add leave balance?
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
    public List<Employee> SearchEmployeeByName(String keyword) {
        return employeeRepository.SearchEmployeeByName(keyword);
    }

    @Override
    @Transactional
    public List<Employee> findEmployeeByJobDesignation(String jobDesignation) {
        return employeeRepository.findEmployeeByJobDesignation(jobDesignation);
    }

    @Override
    @Transactional
    public List<Employee> findUserByRoleType(String roleType) {
        return employeeRepository.findUserByRoleType(roleType);
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
}
