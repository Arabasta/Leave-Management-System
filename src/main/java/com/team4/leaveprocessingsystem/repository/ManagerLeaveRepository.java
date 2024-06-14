package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ManagerLeaveRepository extends JpaRepository<LeaveApplication, Integer> {
    @Query("SELECT L FROM LeaveApplication L WHERE L.reviewingManager.id = :managerId")
    List<LeaveApplication> findSubordinatesLeaveApplicationsByReviewingManager_Id(@Param("managerId") int managerId);
    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);
}
