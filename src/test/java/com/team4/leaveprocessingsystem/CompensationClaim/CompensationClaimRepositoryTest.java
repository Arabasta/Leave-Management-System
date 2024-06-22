package com.team4.leaveprocessingsystem.CompensationClaim;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.team4.leaveprocessingsystem.ObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CompensationClaimRepositoryTest {
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

        // set up Employee object
        testJobDesignation2 = jobDesignationRepository.save(createJobDesignation("Employee"));
        testLeaveBalance2 = leaveBalanceRepository.save(createLeaveBalance());
        Employee e = createEmployee(testJobDesignation2, testLeaveBalance2);
        e.setManager(testManager);
        this.testEmployee = employeeRepository.save(e);

        // set up List<CompensationClaim> for testing
        testCompensationClaimList = new ArrayList<>();
        CompensationClaim modelToSave = createCompensationClaim(testEmployee, testManager);
        testCompensationClaimList.add(compensationClaimRepository.save(modelToSave));
        modelToSave.setClaimStatus(CompensationClaimStatusEnum.UPDATED);
        testCompensationClaimList.add(compensationClaimRepository.save(modelToSave));
        modelToSave.setClaimStatus(CompensationClaimStatusEnum.APPROVED);
        testCompensationClaimList.add(compensationClaimRepository.save(modelToSave));

        System.out.println("startup executed");
    }

    @Test
    public void testFindByClaimingEmployee() {
        List<CompensationClaim> retrievedList = compensationClaimRepository.findByClaimingEmployee(testEmployee);
        List<Integer> retrievedListEmployees = retrievedList.stream().map(x -> x.getClaimingEmployee().getId()).distinct().toList();
        assertThat(retrievedListEmployees.size()).isEqualTo(1);
        assertThat(retrievedListEmployees.get(0)).isEqualTo(testEmployee.getId());
    }

    @Test
    public void testFindExistingByClaimingEmployeeId() {
        List<CompensationClaim> retrievedList = compensationClaimRepository.findExistingByClaimingEmployeeId(testEmployee.getId());
        // test for EmployeeId
        List<Integer> retrievedListIds = retrievedList.stream().map(x -> x.getClaimingEmployee().getId()).toList();
        List<Integer> testCompensationClaimListIds = testCompensationClaimList.stream().map(x -> x.getClaimingEmployee().getId()).toList();
        assertThat(retrievedListIds).containsAll(testCompensationClaimListIds);
        // test for claim status to be APPLIED, UPDATED OR APPROVED
        List<CompensationClaimStatusEnum> retrievedListStatus = retrievedList.stream().map(CompensationClaim::getClaimStatus).toList();
        List<CompensationClaimStatusEnum> testCompensationClaimListStatus = List.of(CompensationClaimStatusEnum.APPLIED, CompensationClaimStatusEnum.UPDATED, CompensationClaimStatusEnum.APPROVED);
        assertThat(testCompensationClaimListStatus).containsAll(retrievedListStatus);
    }

    @Test
    public void testFindByApprovingManager() {
        List<CompensationClaim> retrievedList = compensationClaimRepository.findByApprovingManager(testManager);
        List<Integer> retrievedListManager = retrievedList.stream().map(x -> x.getApprovingManager().getId()).distinct().toList();
        assertThat(retrievedListManager.size()).isEqualTo(1);
        assertThat(retrievedListManager.get(0)).isEqualTo(testManager.getId());
    }

    @AfterEach()
    void teardown() {
        System.out.println("teardown");
        compensationClaimRepository.deleteAll(testCompensationClaimList); // delete compensation-claims
        employeeRepository.delete(testEmployee); // delete employee related entities
        jobDesignationRepository.delete(testJobDesignation2);
        leaveBalanceRepository.delete(testLeaveBalance2);
        managerRepository.delete(testManager); // delete manager related entities
        jobDesignationRepository.delete(testJobDesignation);
        leaveBalanceRepository.delete(testLeaveBalance);
        System.out.println("teardown executed");
    }
}
