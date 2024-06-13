package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
}
