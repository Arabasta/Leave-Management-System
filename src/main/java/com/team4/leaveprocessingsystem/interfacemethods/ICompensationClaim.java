package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;

import java.util.List;
import java.util.Map;

public interface ICompensationClaim {

    boolean save(CompensationClaim compensationClaim);

    boolean isClashWithExistingCompensationClaims(CompensationClaim compensationClaim);

    float calculateOvertimeHours(CompensationClaim compensationClaim);

    float calculateLeaveRequested(CompensationClaim compensationClaim);

    long count();

    CompensationClaim findCompensationClaimById(Integer id);

    CompensationClaim findCompensationClaimIfBelongsToEmployee(Integer id, Employee employee);

    CompensationClaim findCompensationClaimIfBelongsToManagerForReview(Integer id, Manager manager);

    List<CompensationClaim> findCompensationClaimsByEmployee(Employee employee);

    Map<String, List<CompensationClaim>> findCompensationClaimsPendingReviewByManager(Manager manager);

}