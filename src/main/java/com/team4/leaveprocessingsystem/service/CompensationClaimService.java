package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.CompensationClaimNotFoundException;
import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ICompensationClaim;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.repository.CompensationClaimRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompensationClaimService implements ICompensationClaim {

    private final CompensationClaimRepository compensationClaimRepository;
    private final EmployeeService employeeService;

    @Autowired
    public CompensationClaimService(CompensationClaimRepository compensationClaimRepository,
                                    EmployeeService employeeService) {
        this.compensationClaimRepository = compensationClaimRepository;
        this.employeeService = employeeService;
    }

    @Override
    @Transactional
    public CompensationClaim save(CompensationClaim claim) throws ServiceSaveException {
        try {
            return compensationClaimRepository.save(claim);
        } catch (Exception e) {
            throw new ServiceSaveException(claim.getClaimingEmployee().getName(), e);
        }
    }

    @Transactional
    public boolean isClashWithExisting(CompensationClaim targetClaim) {
        List<CompensationClaim> existingClaims = compensationClaimRepository.findExistingByClaimingEmployeeId(targetClaim.getClaimingEmployee().getId());
        // Remove target claim from list if it is being edited, so it will not be invalidated
        if (targetClaim.getId() != null) existingClaims.remove(targetClaim);
        // ref: https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
        // Return true if targetClaim clashes with other claims
        for (CompensationClaim claim : existingClaims) {
            if (targetClaim.getOvertimeStart().isAfter(claim.getOvertimeEnd())
                    || targetClaim.getOvertimeEnd().isBefore(claim.getOvertimeStart())) {
            } else {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public float calculateOvertimeHours(CompensationClaim claim) {
        LocalDateTime start = claim.getOvertimeStart();
        LocalDateTime end = claim.getOvertimeEnd();
        if (start == null || end == null) return 0;
        return (start.isBefore(end)) ? start.until(end, ChronoUnit.HOURS) : 0;
    }

    @Transactional
    public float calculateLeaveRequested(CompensationClaim claim) {
        return (int) (calculateOvertimeHours(claim) / 4) * 0.5f;
    }

    public long count() {
        return compensationClaimRepository.count();
    }

    @Override
    @Transactional
    public CompensationClaim findById(Integer id) {
        return compensationClaimRepository.findById(id).orElse(null);
    }

    @Transactional
    public CompensationClaim findIfBelongsToEmployee(Integer id, Employee employee) {
        CompensationClaim claim = findById(id);
        // Ensure that the CompensationClaim is only accessed by its ClaimingEmployee.
        if (!claim.getClaimingEmployee().getId().equals(employee.getId())) {
            throw new CompensationClaimNotFoundException("Claim does not belong to "+employee.getName()+".");
        }
        return claim;
    }

    @Transactional
    public CompensationClaim findIfBelongsToManagerForReview(Integer id, Manager manager) {
        CompensationClaim claim = findById(id);
        // Ensure that the CompensationClaim is only accessed by its ApprovingManager.
        if (!claim.getApprovingManager().getId().equals(manager.getId())) {
            throw new CompensationClaimNotFoundException("Claim is not assigned to "+manager.getName()+" for review.");
        }
        return claim;
    }

    @Transactional
    public CompensationClaim getNewClaimForEmployee(Employee employee) {
        CompensationClaim claim = new CompensationClaim();
        claim.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
        claim.setClaimingEmployee(employee);
        claim.setApprovingManager(employee.getManager());
        claim.setClaimDateTime(LocalDateTime.now());
        return claim;
    }

    @Transactional
    public void setNewClaimAndSave(CompensationClaim claim) throws ServiceSaveException {
        try {
            claim.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
            claim.setOvertimeHours(calculateOvertimeHours(claim));
            claim.setCompensationLeaveRequested(calculateLeaveRequested(claim));
            claim.setClaimDateTime(LocalDateTime.now());
            save(claim);
        } catch (ServiceSaveException e) {
            throw new ServiceSaveException(claim.toString());
        }
    }

    @Transactional
    public void setUpdateClaimAndSave(CompensationClaim claim) throws ServiceSaveException {
        try {
            claim.setClaimStatus(CompensationClaimStatusEnum.UPDATED);
            claim.setOvertimeHours(calculateOvertimeHours(claim));
            claim.setCompensationLeaveRequested(calculateLeaveRequested(claim));
            claim.setClaimDateTime(LocalDateTime.now());
            save(claim);
        } catch (ServiceSaveException e) {
            throw new ServiceSaveException(claim.toString());
        }
    }

    public List<CompensationClaim> findByEmployee(Employee employee) {
        try {
            return compensationClaimRepository.findByClaimingEmployee(employee);
        } catch (CompensationClaimNotFoundException e) {
            throw new CompensationClaimNotFoundException(employee.getName(), e);
        }
    }

    public List<CompensationClaim> filterByEmployeeListAndManager(List<Employee> list, Manager manager) {
        List<CompensationClaim> output = new java.util.ArrayList<>(List.of());
        for (Employee employee : list) {
            List<CompensationClaim> claims = findByEmployee(employee);
            if (claims != null) {
                for (CompensationClaim claim : claims) {
                    if (claim.getApprovingManager().getId().equals(manager.getId())) {
                        output.add(claim);
                    }
                }
            }
        }
        return output;
    }

    @Transactional
    public Map<String, List<CompensationClaim>> findPendingReviewByManager(Manager manager) {
        Map<String, List<CompensationClaim>> map = new HashMap<>();
        List<Employee> employees = employeeService.findEmployeesByManager(manager);
        for (Employee employee : employees) {
            // Filter for Employee with claims of Applied or Updated status
            List<CompensationClaim> claims = findByEmployee(employee)
                    .stream()
                    .filter(claim -> claim.getClaimStatus() == CompensationClaimStatusEnum.APPLIED
                        || claim.getClaimStatus() == CompensationClaimStatusEnum.UPDATED)
                    .toList();
            // If employee has such claims, put into map
            if (!claims.isEmpty()) {
                map.put(employee.getName(), claims);
            }
        }
        return map;
    }

    @Transactional
    public List<CompensationClaim> findByApprovingManager(Manager manager) {
        try {
            return compensationClaimRepository.findByApprovingManager(manager);
        } catch (CompensationClaimNotFoundException e) {
            throw new CompensationClaimNotFoundException("Approving Manager: "+manager.getName(), e);
        }
    }
}
