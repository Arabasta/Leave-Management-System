package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveBalance;

public interface ILeaveBalance {
    boolean save(LeaveBalance leaveBalance);
    LeaveBalance findByEmployee(int employee_id);
}
