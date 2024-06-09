package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;

import java.util.List;

public interface ICompensationClaim {
    boolean save(CompensationClaim compensationClaim);
    List<CompensationClaim> findCompensationClaimsByEmployee(Employee employee);
    long count();
    float overtimeHours(CompensationClaim compensationClaim);
    CompensationClaim findCompensationClaim(Integer id);
    CompensationClaim changeCompensationClaim(CompensationClaim compensationClaim);

}