package com.team4.leaveprocessingsystem.JobDesignation;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.repository.JobDesignationRepository;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import com.team4.leaveprocessingsystem.repository.ManagerRepository;
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
public class JobDesignationRepositoryTest {
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
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

    @BeforeEach()
    void init() {
        System.out.println("startup");
        // set up Manager object
        testJobDesignation = jobDesignationRepository.save(createJobDesignation("Manager"));
        testLeaveBalance = leaveBalanceRepository.save(createLeaveBalance());
        testManager = managerRepository.save(createManager(testJobDesignation, testLeaveBalance));

        // set up Employee object
        testJobDesignation2 = jobDesignationRepository.save(createJobDesignation("Employee"));
        testLeaveBalance2 = leaveBalanceRepository.save(createLeaveBalance());
        Employee e = createEmployee(testJobDesignation2, testLeaveBalance2);
        e.setManager(testManager);
        this.testEmployee = employeeRepository.save(e);

        System.out.println("startup executed");
    }

    @Test
    public void testFindByName() {
        Optional<JobDesignation> retrievedJobDesignation = jobDesignationRepository.findByName("ObjectMotherJobDesignation");
        assertThat(retrievedJobDesignation).isNotNull();
    }

    @Test
    public void testQueryJobDesignationByName() {
        List<JobDesignation> retrievedJobDesignations = jobDesignationRepository.queryJobDesignationsByName("Employee");
        assertThat(retrievedJobDesignations).size().isEqualTo(1);
        assertThat(retrievedJobDesignations.get(0).getId()).isEqualTo(testEmployee.getJobDesignation().getId());

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
