package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CompensationClaimSeeder {

    private final CompensationClaimService compensationClaimService;
    private final EmployeeService employeeService;

    public CompensationClaimSeeder(CompensationClaimService compensationClaimService,
                                  EmployeeService employeeService) {
        this.compensationClaimService = compensationClaimService;
        this.employeeService = employeeService;
    }

    public void seed() {
        if (compensationClaimService.count() == 0) {
            Employee employee = employeeService.findByName("Employee");
            Manager manager = employeeService.findManagerByName("Manager");

            CompensationClaim compensationClaim1 = new CompensationClaim();
            compensationClaim1.setClaimingEmployee(employee);
            compensationClaim1.setCompensationClaimStatus(CompensationClaimStatusEnum.APPROVED);
            compensationClaim1.setCompensationLeaveRequested(0.5f);
            compensationClaim1.setOvertimeStartDateTime(LocalDateTime.now().minusDays(20).minusHours(4));
            compensationClaim1.setOvertimeEndDateTime(LocalDateTime.now().minusDays(20));
            compensationClaim1.setApprovingManager(manager);
            compensationClaimService.save(compensationClaim1);

            CompensationClaim compensationClaim2 = new CompensationClaim();
            compensationClaim2.setClaimingEmployee(employee);
            compensationClaim2.setApprovingManager(manager);
            compensationClaim2.setCompensationLeaveRequested(0.5f);
            compensationClaim2.setOvertimeStartDateTime(LocalDateTime.now().minusDays(18).minusHours(4));
            compensationClaim2.setOvertimeEndDateTime(LocalDateTime.now().minusDays(18));
            compensationClaim2.setCompensationClaimStatus(CompensationClaimStatusEnum.REJECTED);
            compensationClaim2.setComments("You went for 3 hours lunch. Your Overtime work was only 2 hours.");
            compensationClaimService.save(compensationClaim2);

            CompensationClaim compensationClaim3 = new CompensationClaim();
            compensationClaim3.setClaimingEmployee(employee);
            compensationClaim3.setCompensationClaimStatus(CompensationClaimStatusEnum.APPLIED);
            compensationClaim3.setCompensationLeaveRequested(1.0f);
            compensationClaim3.setOvertimeStartDateTime(LocalDateTime.now().minusDays(15).minusHours(8));
            compensationClaim3.setOvertimeEndDateTime(LocalDateTime.now().minusDays(15));
            compensationClaim3.setApprovingManager(manager);
            compensationClaimService.save(compensationClaim3);
        }
    }
}
