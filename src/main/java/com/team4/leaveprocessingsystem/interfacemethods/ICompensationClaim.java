package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;

import java.util.List;
import java.util.Map;

public interface ICompensationClaim {

    CompensationClaim save(CompensationClaim claim);

    boolean isClashWithExisting(CompensationClaim claim);

    float calculateOvertimeHours(CompensationClaim claim);

    float calculateLeaveRequested(CompensationClaim claim);

    long count();

    CompensationClaim findById(Integer id);

    CompensationClaim findIfBelongsToEmployee(Integer id, Employee employee);

    CompensationClaim findIfBelongsToManagerForReview(Integer id, Manager manager);

    CompensationClaim getNewClaimForEmployee(Employee employee);

    void setNewClaimAndSave(CompensationClaim claim);

    void setUpdateClaimAndSave(CompensationClaim claim);

    List<CompensationClaim> findByEmployee(Employee employee);

    Map<String, List<CompensationClaim>> findPendingReviewByManager(Manager manager);

}