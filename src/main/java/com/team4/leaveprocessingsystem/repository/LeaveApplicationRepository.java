package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);

    @Query("Select distinct l.reviewingManager.id from LeaveApplication l")
    List<Integer> findReviewingManagersIds();

    @Query("Select distinct l.submittingEmployee.id from LeaveApplication l")
    List<Integer> findSubmittingEmployeesIds();

}
