package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompensationClaimRepository extends JpaRepository<CompensationClaim, Integer> {
    List<CompensationClaim> findByClaimingEmployee(Employee employee);
}
