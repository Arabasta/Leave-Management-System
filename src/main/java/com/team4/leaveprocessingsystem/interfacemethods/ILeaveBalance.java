package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;

public interface ILeaveBalance {
    boolean save(LeaveBalance leaveBalance);
    void update(LeaveApplication leaveApplication);
    void refund(LeaveApplication leaveApplication);
    void updateCompensationLeave(CompensationClaim claim);
    LeaveBalance findByEmployee(int employee_id);
    LeaveBalance findLeaveBalanceById(int id);
}
