package com.team4.leaveprocessingsystem;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 * This class contains base methods to create Entities for testing in the @SpringBootTest classes (e.g., Employee and Manager can be created for CompensationClaim tests).
 */
public class ObjectMother {

    private static final LocalDateTime fixedLocalDateTimeNow = LocalDateTime.now(Clock.fixed(Instant.parse("2023-12-03T10:15:30.00Z"), ZoneId.systemDefault()));

    public static JobDesignation createJobDesignation(String name) {
        return new JobDesignation(
                "ObjectMotherJobDesignation" + name,
                15);
    }

    public static LeaveBalance createLeaveBalance() {
        return new LeaveBalance(15);
    }

    public static Employee createEmployee(JobDesignation jobDesignation, LeaveBalance leaveBalance) {
        return new Employee(
                "ObjectMotherEmployee",
                jobDesignation,
                null,
                leaveBalance);
    }

    public static Manager createManager(JobDesignation jobDesignation, LeaveBalance leaveBalance) {
        return new Manager(
                "ObjectMotherManager",
                jobDesignation,
                null,
                leaveBalance);
    }

    public static CompensationClaim createCompensationClaim(Employee claimingEmployee,
                                                            Manager approvingManager) {
        return new CompensationClaim(
                CompensationClaimStatusEnum.APPLIED,
                claimingEmployee,
                fixedLocalDateTimeNow,
                fixedLocalDateTimeNow.minusDays(4),
                fixedLocalDateTimeNow.minusDays(3).minusHours(20),
                4f,
                0.5f,
                approvingManager,
                fixedLocalDateTimeNow.minusDays(2),
                "ObjectMotherCompensationClaimComments"
        );
    }

}
