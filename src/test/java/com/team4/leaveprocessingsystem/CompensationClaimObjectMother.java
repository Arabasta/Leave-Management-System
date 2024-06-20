package com.team4.leaveprocessingsystem;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;

import java.util.List;

public class CompensationClaimObjectMother {

    static JobDesignation createCompensationClaimObjectMotherJobDesignation(String name) {
        return new JobDesignation(
                "CompensationClaimObjectMotherJobDesignation"+name,
                15);
    }

    static LeaveBalance createCompensationClaimObjectMotherLeaveBalance() {
        return new LeaveBalance();
    }

    static Employee createCompensationClaimObjectMotherEmployee(JobDesignation jobDesignation,
                                                                LeaveBalance leaveBalance) {

        return new Employee(
                "CompensationClaimObjectMotherEmployee",
                jobDesignation,
                null,
                leaveBalance);
    }

    static Manager createCompensationClaimObjectMotherManager(JobDesignation jobDesignation,
                                                              LeaveBalance leaveBalance) {

        return new Manager(
                "CompensationClaimObjectMotherManager",
                jobDesignation,
                null,
                leaveBalance);
    }

}
