package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ICompensationClaim;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.repository.CompensationClaimRepository;
import com.team4.leaveprocessingsystem.util.DateTimeCounterUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompensationClaimService implements ICompensationClaim {

    private final CompensationClaimRepository compensationClaimRepository;

    @Autowired
    public CompensationClaimService(CompensationClaimRepository compensationClaimRepository) {
        this.compensationClaimRepository = compensationClaimRepository;
    }
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
            return compensationClaimRepository.findByClaimingEmployee(employee);
        } catch (NoSuchElementException e) {
            // todo: whoever's incharge of this, add a proper exception
            return null;
        }
    }

    public long count() {
        return compensationClaimRepository.count();
    }

    @Override
    @Transactional
    public float overtimeHours(CompensationClaim compensationClaim) {
        LocalDateTime start = compensationClaim.getOvertimeStartDateTime();
        LocalDateTime end = compensationClaim.getOvertimeEndDateTime();
        if (start == null || end == null) return 0;
        return (start.isBefore(end)) ? start.until(end, ChronoUnit.HOURS) : 0;
    }

    @Override
    @Transactional
    public float compensationLeaveRequested(float overtimeHours) {
        return (int) (overtimeHours / 4) * 0.5f;
    }

    @Override
    @Transactional
    public CompensationClaim findCompensationClaim(Integer id) {
        return compensationClaimRepository.findById(id).orElse(null);
    }

}
