package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ICompensationClaim;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.repository.CompensationClaimRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompensationClaimService implements ICompensationClaim {
    @Autowired
    CompensationClaimRepository compensationClaimRepository;

    @Override
    @Transactional
    public boolean save(CompensationClaim compensationClaim) {
        try {
            compensationClaimRepository.save(compensationClaim);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException("compensation claim from: " +
                    compensationClaim.getClaimingEmployee().getName(), e);
        }
    }

    public List<CompensationClaim> findCompensationClaimsByEmployee(Employee employee) {
        try {
            return employee.getCompensationClaims();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public long count() {
        return compensationClaimRepository.count();
    }

    public float overtimeHours(CompensationClaim compensationClaim) {
        LocalDateTime start = compensationClaim.getOvertimeStartDateTime();
        LocalDateTime end = compensationClaim.getOvertimeEndDateTime();
        return (start.isBefore(end)) ? start.until(end, ChronoUnit.HOURS) :  0;
    }
}
