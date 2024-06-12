package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;

import java.util.List;

public interface ILeaveApplication {
    boolean save(LeaveApplication leaveApplication);
    LeaveApplication findLeaveApplicationById(Integer id);
    List<LeaveApplication> findSubordinatesLeaveApplicationsByReviewingManager_Id(int managerId);
    long count();
    LeaveApplication getLeaveApplicationIfBelongsToEmployee(int id, Employee employee);
}
