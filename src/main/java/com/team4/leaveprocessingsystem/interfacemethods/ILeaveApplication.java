package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.LeaveApplication;

public interface ILeaveApplication {
    boolean save(LeaveApplication leaveApplication);
    long count();
}
