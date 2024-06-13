package com.team4.leaveprocessingsystem;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.repository.*;
import com.team4.leaveprocessingsystem.service.LeaveTypeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.team4.leaveprocessingsystem.CompensationClaimObjectMother.*;
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
    @Autowired
    private LeaveTypeService leaveTypeService;

    private final LocalDateTime fixedNow;
    private Manager testManager;
    private Employee testEmployee;
    private JobDesignation testJobDesignation;
    private LeaveBalance testLeaveBalance;
    private JobDesignation testJobDesignation2;
    private LeaveBalance testLeaveBalance2;

    public CompensationClaimRepositoryTest() {
        this.fixedNow = LocalDateTime.now(Clock.fixed(Instant.parse("2023-12-03T10:15:30.00Z"), ZoneId.systemDefault()));
    }

    @BeforeEach()
    void init() {
        System.out.println("startup");
        this.testJobDesignation = jobDesignationRepository
                .save(createCompensationClaimObjectMotherJobDesignation(leaveTypeService.findAllLeaveTypes(),"Manager"));
        this.testLeaveBalance = leaveBalanceRepository
                .save(createCompensationClaimObjectMotherLeaveBalance());
        Manager m = createCompensationClaimObjectMotherManager(testJobDesignation, testLeaveBalance);
        this.testManager = managerRepository.save(m);

        this.testJobDesignation2 = jobDesignationRepository
                .save(createCompensationClaimObjectMotherJobDesignation(leaveTypeService.findAllLeaveTypes(),"Employee"));
        this.testLeaveBalance2 = leaveBalanceRepository
                .save(createCompensationClaimObjectMotherLeaveBalance());
        Employee e = createCompensationClaimObjectMotherEmployee(testJobDesignation2, testLeaveBalance2);
        e.setManager(this.testManager);
        this.testEmployee = employeeRepository.save(e);
        System.out.println("startup executed");
    }

    @Test
    public void testSaveEntity() {
        CompensationClaim modelToSave = new CompensationClaim();
        modelToSave.setCompensationClaimStatus(CompensationClaimStatusEnum.REJECTED);
        modelToSave.setClaimingEmployee(testEmployee);
        modelToSave.setClaimDateTime(fixedNow);
        modelToSave.setOvertimeStartDateTime(fixedNow.minusDays(4));
        modelToSave.setOvertimeEndDateTime(fixedNow.minusDays(3).minusHours(20));
        modelToSave.setOvertimeHours(4f);
        modelToSave.setCompensationLeaveRequested(0.5f);
        modelToSave.setApprovingManager(testManager);
        modelToSave.setReviewedDateTime(fixedNow.minusDays(2));
        modelToSave.setComments("You were late on that day.");
        CompensationClaim savedModel = compensationClaimRepository.save(modelToSave);

        assertThat(savedModel.getId()).isNotNull();
        assertThat(savedModel.getCompensationClaimStatus()).isEqualTo(CompensationClaimStatusEnum.REJECTED);
        assertThat(savedModel.getClaimingEmployee()).isEqualTo(testEmployee);
        assertThat(savedModel.getClaimDateTime()).isEqualTo(fixedNow);
        assertThat(savedModel.getOvertimeStartDateTime()).isEqualTo(fixedNow.minusDays(4));
        assertThat(savedModel.getOvertimeEndDateTime()).isEqualTo(fixedNow.minusDays(3).minusHours(20));
        assertThat(savedModel.getOvertimeHours()).isEqualTo(4f);
        assertThat(savedModel.getCompensationLeaveRequested()).isEqualTo(0.5f);
        assertThat(savedModel.getApprovingManager()).isEqualTo(testManager);
        assertThat(savedModel.getReviewedDateTime()).isEqualTo(fixedNow.minusDays(2));
        assertThat(savedModel.getComments()).isEqualTo("You were late on that day.");

        compensationClaimRepository.delete(savedModel);
    }

    @AfterEach()
    void teardown() {
        System.out.println("teardown");
        employeeRepository.delete(testEmployee);
        jobDesignationRepository.delete(testJobDesignation2);
        leaveBalanceRepository.delete(testLeaveBalance2);

        managerRepository.delete(testManager);
        jobDesignationRepository.delete(testJobDesignation);
        leaveBalanceRepository.delete(testLeaveBalance);
        System.out.println("teardown executed");
    }
}
