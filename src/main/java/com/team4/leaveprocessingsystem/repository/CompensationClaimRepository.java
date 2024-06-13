package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompensationClaimRepository extends JpaRepository<CompensationClaim, Integer> {
    List<CompensationClaim> findByClaimingEmployee(Employee employee);

    @Query("Select distinct c.approvingManager.id from CompensationClaim c")
    List<Integer> findApprovingManagersIds();

    @Query("Select distinct c.claimingEmployee.id from CompensationClaim c")
    List<Integer> findClaimingEmployeesIds();
}
