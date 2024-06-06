package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {
}
