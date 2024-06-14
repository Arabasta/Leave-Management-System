package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompensationClaimRepository extends JpaRepository<CompensationClaim, Integer> {
    List<CompensationClaim> findByClaimingEmployee(Employee employee);

    @Query("Select c FROM CompensationClaim c " +
            "WHERE c.claimingEmployee.id = :employee_id " +
            "AND c.claimStatus = 'APPLIED'" +
            "AND c.claimStatus = 'UPDATED'" +
            "AND c.claimStatus = 'APPROVED'")
    List<CompensationClaim> findExistingByClaimingEmployeeId(@Param("employee_id") int employee_id);
}
