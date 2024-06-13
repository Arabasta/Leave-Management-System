package com.team4.leaveprocessingsystem.CompensationClaim;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.team4.leaveprocessingsystem.CompensationClaim.CompensationClaimObjectMother.*;
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

    private final LocalDateTime fixedNow;
    private Manager testManager;
    private Employee testEmployee;
    private JobDesignation testJobDesignation;
    private LeaveBalance testLeaveBalance;
    private JobDesignation testJobDesignation2;
    private LeaveBalance testLeaveBalance2;
    private List<CompensationClaim> testCompensationClaimList;

    public CompensationClaimRepositoryTest() {
        this.fixedNow = LocalDateTime.now(Clock.fixed(Instant.parse("2023-12-03T10:15:30.00Z"), ZoneId.systemDefault()));
    }

    @BeforeEach()
    void init() {
        System.out.println("startup");
        testJobDesignation = jobDesignationRepository
                .save(createCompensationClaimObjectMotherJobDesignation("Manager"));
        testLeaveBalance = leaveBalanceRepository
                .save(createCompensationClaimObjectMotherLeaveBalance());
        Manager m = createCompensationClaimObjectMotherManager(testJobDesignation, testLeaveBalance);
        testManager = managerRepository.save(m);

        testJobDesignation2 = jobDesignationRepository
                .save(createCompensationClaimObjectMotherJobDesignation("Employee"));
        testLeaveBalance2 = leaveBalanceRepository
                .save(createCompensationClaimObjectMotherLeaveBalance());
        Employee e = createCompensationClaimObjectMotherEmployee(testJobDesignation2, testLeaveBalance2);
        e.setManager(testManager);
        this.testEmployee = employeeRepository.save(e);

        testCompensationClaimList = new ArrayList<>();
        CompensationClaim modelToSave = new CompensationClaim();
        modelToSave.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
        modelToSave.setClaimingEmployee(testEmployee);
        modelToSave.setClaimDateTime(fixedNow);
        modelToSave.setOvertimeStart(fixedNow.minusDays(4));
        modelToSave.setOvertimeEnd(fixedNow.minusDays(3).minusHours(20));
        modelToSave.setOvertimeHours(4f);
        modelToSave.setCompensationLeaveRequested(0.5f);
        modelToSave.setApprovingManager(testManager);
        modelToSave.setReviewedDateTime(fixedNow.minusDays(2));
        modelToSave.setComments("You were late on that day.");
        CompensationClaim savedModel = compensationClaimRepository.save(modelToSave);
        testCompensationClaimList.add(savedModel);

        CompensationClaim modelToSave2 = new CompensationClaim();
        modelToSave2.setClaimStatus(CompensationClaimStatusEnum.UPDATED);
        modelToSave2.setClaimingEmployee(testEmployee);
        modelToSave2.setClaimDateTime(fixedNow);
        modelToSave2.setOvertimeStart(fixedNow.minusDays(4));
        modelToSave2.setOvertimeEnd(fixedNow.minusDays(3).minusHours(20));
        modelToSave2.setOvertimeHours(4f);
        modelToSave2.setCompensationLeaveRequested(0.5f);
        modelToSave2.setApprovingManager(testManager);
        modelToSave2.setReviewedDateTime(fixedNow.minusDays(2));
        modelToSave2.setComments("You were late on that day.");
        CompensationClaim savedModel2 = compensationClaimRepository.save(modelToSave2);
        testCompensationClaimList.add(savedModel2);

        CompensationClaim modelToSave3 = new CompensationClaim();
        modelToSave3.setClaimStatus(CompensationClaimStatusEnum.APPROVED);
        modelToSave3.setClaimingEmployee(testEmployee);
        modelToSave3.setClaimDateTime(fixedNow);
        modelToSave3.setOvertimeStart(fixedNow.minusDays(4));
        modelToSave3.setOvertimeEnd(fixedNow.minusDays(3).minusHours(20));
        modelToSave3.setOvertimeHours(4f);
        modelToSave3.setCompensationLeaveRequested(0.5f);
        modelToSave3.setApprovingManager(testManager);
        modelToSave3.setReviewedDateTime(fixedNow.minusDays(2));
        modelToSave3.setComments("You were late on that day.");
        CompensationClaim savedModel3 = compensationClaimRepository.save(modelToSave3);
        testCompensationClaimList.add(savedModel3);

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
