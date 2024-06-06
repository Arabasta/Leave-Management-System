package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
}
