package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    List<LeaveApplication> findSubordinatesLeaveApplicationsByReviewingManager_Id(int managerId);
    List<LeaveApplication> findLeaveApplicationsById(int id);
    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);

}
