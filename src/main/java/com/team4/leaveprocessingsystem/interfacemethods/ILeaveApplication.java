package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ILeaveApplication {
    boolean save(LeaveApplication leaveApplication);
    LeaveApplication findLeaveApplicationById(Integer id);
    long count();
    LeaveApplication getLeaveApplicationIfBelongsToEmployee(int id, Employee employee);

    @Transactional
    List<Integer> allReviewingManagersIds();

    @Transactional
    List<Integer> allClaimingEmployees();
}
