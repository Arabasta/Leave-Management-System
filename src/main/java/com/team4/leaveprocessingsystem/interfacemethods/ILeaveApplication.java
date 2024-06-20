package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ILeaveApplication {
    boolean save(LeaveApplication leaveApplication);
    LeaveApplication findLeaveApplicationById(Integer id);
    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);
    LeaveApplication getLeaveApplicationIfBelongsToEmployee(int id, int employeeId);
    Page<LeaveApplication> findBySubmittingEmployeeWithPaging(Employee submittingEmployee, Pageable page);
    Map<String, List<LeaveApplication>> findLeaveApplicationsPendingApprovalByManager(Manager manager);
    long count();
    Page<LeaveApplication> filterManagerViewSearch(int managerId,
                                                   String keyword,
                                                   String searchType,
                                                   String startDate,
                                                   String endDate,
                                                   String leaveStatus,
                                                   Pageable page);
    Map<String, List<LeaveApplication>> mapEmployeeOnLeave(String targetYearMonth);
}
