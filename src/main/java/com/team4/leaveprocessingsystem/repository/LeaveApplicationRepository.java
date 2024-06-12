package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);

}
