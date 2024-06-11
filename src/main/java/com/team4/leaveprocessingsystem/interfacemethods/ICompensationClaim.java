package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;

import java.util.List;

public interface ICompensationClaim {
    boolean save(CompensationClaim compensationClaim);

    List<CompensationClaim> findCompensationClaimsByEmployee(Employee employee);

    long count();

    CompensationClaim findCompensationClaim(Integer id);

    float overtimeHours(CompensationClaim compensationClaim);

    float compensationLeaveRequested(float overtimeHours);
}