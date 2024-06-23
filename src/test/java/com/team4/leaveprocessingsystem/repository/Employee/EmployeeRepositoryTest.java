package com.team4.leaveprocessingsystem.repository.Employee;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.team4.leaveprocessingsystem.ObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EmployeeRepositoryTest {
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompensationClaimRepository compensationClaimRepository;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private JobDesignationRepository jobDesignationRepository;

    private Manager testManager;
    private Employee testEmployee;
    private JobDesignation testJobDesignation;
    private LeaveBalance testLeaveBalance;
    private JobDesignation testJobDesignation2;
    private LeaveBalance testLeaveBalance2;
    private List<CompensationClaim> testCompensationClaimList;

    @BeforeEach()
    void init() {
        System.out.println("startup");
        // set up Manager object
        testJobDesignation = jobDesignationRepository.save(createJobDesignation("Manager"));
        testLeaveBalance = leaveBalanceRepository.save(createLeaveBalance());
        testManager = managerRepository.save(createManager(testJobDesignation, testLeaveBalance));

        // set up Employee object for testing
        testJobDesignation2 = jobDesignationRepository.save(createJobDesignation("Employee"));
        testLeaveBalance2 = leaveBalanceRepository.save(createLeaveBalance());
        Employee e = createEmployee(testJobDesignation2, testLeaveBalance2);
        e.setManager(testManager);
        this.testEmployee = employeeRepository.save(e);

        System.out.println("startup executed");
    }

    @Test
    public void testFindByName() {
        Optional<Employee> retrievedEmployee = employeeRepository.findByName("ObjectMotherEmployee");
        assertThat(retrievedEmployee).isNotNull();
    }

    @Test
    public void testFindByManager() {
        List<Employee> retrievedEmployees = employeeRepository.findByManager(testManager);
        assertThat(retrievedEmployees.size()).isEqualTo(1);
        assertThat(retrievedEmployees.get(0).getId()).isEqualTo(testEmployee.getId());
    }

    @Test
    public void testFindEmployeesByName() {
        List<Employee> retrievedEmployees = employeeRepository.findEmployeesByName("ObjectMotherEmployee");
        assertThat(retrievedEmployees.size()).isEqualTo(1);
        assertThat(retrievedEmployees.get(0).getId()).isEqualTo(testEmployee.getId());
    }

    @Test
    public void testFindEmployeesByJobDesignation() {
        List<Employee> retrievedEmployees = employeeRepository.findEmployeesByJobDesignation(testEmployee.getJobDesignation().getName());
        assertThat(retrievedEmployees.size()).isEqualTo(1);
        assertThat(retrievedEmployees.get(0).getId()).isEqualTo(testEmployee.getId());
    }

    @Test
    public void testFindEmployeesByManager() {
        List<Employee> retrievedEmployees = employeeRepository.findEmployeesByManager(testEmployee.getManager().getName());
        assertThat(retrievedEmployees.size()).isEqualTo(1);
        assertThat(retrievedEmployees.get(0).getId()).isEqualTo(testEmployee.getId());
    }

    @Test
    public void testFindAllExcludedDeleted() {
        List<Employee> retrievedEmployees = employeeRepository.findAllExcludeDeleted();
        assertThat(retrievedEmployees).isNotNull();
    }

    @Test
    public void testOnlyDeleted() {
        List<Employee> retrievedEmployees = employeeRepository.findOnlyDeleted();
        assertThat(retrievedEmployees).isEmpty();
    }

    @AfterEach()
    void teardown() {
        System.out.println("teardown");
        employeeRepository.delete(testEmployee); // delete employee related entities
        jobDesignationRepository.delete(testJobDesignation2);
        leaveBalanceRepository.delete(testLeaveBalance2);

        managerRepository.delete(testManager); // delete manager related entities
        jobDesignationRepository.delete(testJobDesignation);
        leaveBalanceRepository.delete(testLeaveBalance);

        System.out.println("teardown executed");
    }

}
