package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.RollbackException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

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

    private static final Logger log = (Logger) LoggerFactory.getLogger(EmployeeService.class);

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
            employeeRepository.save(employee);
            return true;
        } catch (Exception e) {
            log.error("Error saving employee: " + employee.getName(), e);
            handleException(e);

            throw new ServiceSaveException("Failed to save employee: " + employee.getName(), e);
        }
    }

    private void handleException(Exception e) {
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e;
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                log.error("Validation error: " + violation.getPropertyPath() + " - " + violation.getMessage());
            }
        } else if (e instanceof DataIntegrityViolationException) {
            DataIntegrityViolationException dive = (DataIntegrityViolationException) e;
            log.error("Data integrity violation: " + dive.getMostSpecificCause().getMessage());
        } else if (e instanceof EntityExistsException) {
            log.error("Entity already exists: " + e.getMessage());
        } else if (e instanceof TransactionSystemException) {
            TransactionSystemException tse = (TransactionSystemException) e;
            if (tse.getCause() instanceof RollbackException) {
                RollbackException re = (RollbackException) tse.getCause();
                log.error("Transaction rollback: " + re.getMessage());
            }
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
