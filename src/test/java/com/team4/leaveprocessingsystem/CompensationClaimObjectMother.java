package com.team4.leaveprocessingsystem;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;

import java.util.List;

public class CompensationClaimObjectMother {

    static LeaveType createCompensationClaimObjectMotherLeaveTypeCompensation() {
        LeaveType compensationLeave = new LeaveType();
        compensationLeave.setLeaveType(LeaveTypeEnum.COMPENSATION);
        compensationLeave.setDescription("This leave is offered to compensate employees \n" +
                "for the extra hours they have worked beyond their regular working hours.");
        return compensationLeave;
    }

    static JobDesignation createCompensationClaimObjectMotherJobDesignation(List<LeaveType> leaveTypeList, String name) {
        return new JobDesignation(
                "CompensationClaimObjectMotherJobDesignation"+name,
                15,
                leaveTypeList);
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
