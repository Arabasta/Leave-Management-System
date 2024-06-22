package com.team4.leaveprocessingsystem.Manager;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.team4.leaveprocessingsystem.ObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ManagerRepositoryTest {
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

        System.out.println("startup executed");
    }

    @Test
    public void testFindManagerByName() {
        List<Employee> retrievedManagers = managerRepository.findManagerByName("Manager");
        assertThat(retrievedManagers.size()).isEqualTo(1);
        assertThat(retrievedManagers.get(0).getName()).isEqualTo("ObjectMotherManager");
    }

    @AfterEach()
    void teardown() {
        System.out.println("teardown");

        managerRepository.delete(testManager); // delete manager related entities
        jobDesignationRepository.delete(testJobDesignation);
        leaveBalanceRepository.delete(testLeaveBalance);

        System.out.println("teardown executed");
    }
}
