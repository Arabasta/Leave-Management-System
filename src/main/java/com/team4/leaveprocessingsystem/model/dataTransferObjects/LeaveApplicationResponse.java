package com.team4.leaveprocessingsystem.model.dataTransferObjects;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LeaveApplicationResponse {
    private Employee employee;
    private List<LeaveApplication> leaveApplicationList;
    private LeaveBalance leaveBalance;
    private List<LeaveTypeEnum> leaveTypeList;

    public LeaveApplicationResponse(Employee employee, List<LeaveApplication> leaveApplicationList, LeaveBalance leaveBalance) {
        this.employee = employee;
        this.leaveApplicationList = leaveApplicationList;
        this.leaveBalance = leaveBalance;
        this.leaveTypeList = new ArrayList<>(List.of(LeaveTypeEnum.values()));
    }
}
