package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

public interface ILeaveApplication {
    boolean save(LeaveApplication leaveApplication);
    LeaveApplication findLeaveApplicationById(Integer id);
    List<LeaveApplication> findSubordinatesLeaveApplicationsByReviewingManager_Id(int managerId);
    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);
    List<LeaveApplication> findByEmployeeName(String name);
    List<LeaveApplication> findByEmployeeId(int Id);
    LeaveApplication getLeaveApplicationIfBelongsToEmployee(int id, Employee employee);
    Map<String, List<LeaveApplication>> findLeaveApplicationsPendingApprovalByManager(Manager manager);
    long count();
}
