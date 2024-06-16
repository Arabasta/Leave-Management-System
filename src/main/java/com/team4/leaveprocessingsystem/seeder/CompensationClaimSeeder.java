package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CompensationClaimSeeder {

    private final CompensationClaimService compensationClaimService;
    private final EmployeeService employeeService;
    private final LeaveBalanceService leaveBalanceService;
    private final EmployeeRepository employeeRepository;

    public CompensationClaimSeeder(CompensationClaimService compensationClaimService,
                                   EmployeeService employeeService,
                                   LeaveBalanceService leaveBalanceService, EmployeeRepository employeeRepository) {
        this.compensationClaimService = compensationClaimService;
        this.employeeService = employeeService;
        this.leaveBalanceService = leaveBalanceService;
        this.employeeRepository = employeeRepository;
    }

    public void seed() {
        if (compensationClaimService.count() == 0) {
            System.out.println("Start Compensation Claim Seeder");

            Employee employee = employeeService.findByName("Anya Forger");
            Manager manager = employee.getManager();
            LeaveBalance employeeLeaveBalance = leaveBalanceService.findByEmployee(employee.getId());

            CompensationClaim compensationClaim1 = new CompensationClaim();
            compensationClaim1.setClaimingEmployee(employee);
            compensationClaim1.setClaimStatus(CompensationClaimStatusEnum.APPROVED);
            compensationClaim1.setCompensationLeaveRequested(0.5f);
            compensationClaim1.setOvertimeStart(LocalDateTime.now().minusDays(20).minusHours(4));
            compensationClaim1.setOvertimeEnd(LocalDateTime.now().minusDays(20));
            compensationClaim1.setClaimDateTime(LocalDateTime.now().minusDays(19));
            compensationClaim1.setOvertimeHours(4L);
            compensationClaim1.setApprovingManager(manager);
            compensationClaim1.setReviewedDateTime(LocalDateTime.now().minusDays(19).minusHours(3));
            employeeLeaveBalance.setCompensationLeave(0.5f);
            compensationClaimService.save(compensationClaim1);
            leaveBalanceService.save(employeeLeaveBalance);

            CompensationClaim compensationClaim2 = new CompensationClaim();
            compensationClaim2.setClaimingEmployee(employee);
            compensationClaim2.setApprovingManager(manager);
            compensationClaim2.setCompensationLeaveRequested(0.5f);
            compensationClaim2.setOvertimeStart(LocalDateTime.now().minusDays(18).minusHours(4));
            compensationClaim2.setOvertimeEnd(LocalDateTime.now().minusDays(18));
            compensationClaim2.setClaimDateTime(LocalDateTime.now().minusDays(17));
            compensationClaim2.setOvertimeHours(4L);
            compensationClaim2.setClaimStatus(CompensationClaimStatusEnum.REJECTED);
            compensationClaim2.setComments("You went for 3 hours lunch. Your Overtime work was only 2 hours.");
            compensationClaim2.setReviewedDateTime(LocalDateTime.now().minusDays(17).minusHours(1));
            compensationClaimService.save(compensationClaim2);

            CompensationClaim compensationClaim3 = new CompensationClaim();
            compensationClaim3.setClaimingEmployee(employee);
            compensationClaim3.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
            compensationClaim3.setCompensationLeaveRequested(1.0f);
            compensationClaim3.setOvertimeStart(LocalDateTime.now().minusDays(15).minusHours(8));
            compensationClaim3.setOvertimeEnd(LocalDateTime.now().minusDays(15));
            compensationClaim3.setClaimDateTime(LocalDateTime.now().minusDays(14));
            compensationClaim3.setOvertimeHours(8L);
            compensationClaim3.setApprovingManager(manager);
            employeeLeaveBalance.setCompensationLeave(1.0f);
            compensationClaimService.save(compensationClaim3);

            CompensationClaim compensationClaim4 = new CompensationClaim();
            compensationClaim4.setClaimingEmployee(employee);
            compensationClaim4.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
            compensationClaim4.setCompensationLeaveRequested(1.0f);
            compensationClaim4.setOvertimeStart(LocalDateTime.now().minusDays(13).minusHours(8));
            compensationClaim4.setOvertimeEnd(LocalDateTime.now().minusDays(13));
            compensationClaim4.setClaimDateTime(LocalDateTime.now().minusDays(12));
            compensationClaim4.setOvertimeHours(8L);
            compensationClaim4.setApprovingManager(manager);
            employeeLeaveBalance.setCompensationLeave(1.0f);
            compensationClaimService.save(compensationClaim4);

            Employee employee2 = employeeService.findByName("Andrew");
            LeaveBalance employeeLeaveBalance2 = leaveBalanceService.findByEmployee(employee2.getId());

            CompensationClaim compensationClaim5 = new CompensationClaim();
            compensationClaim5.setClaimingEmployee(employee2);
            compensationClaim5.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
            compensationClaim5.setCompensationLeaveRequested(0.5f);
            compensationClaim5.setOvertimeStart(LocalDateTime.now().minusDays(20).minusHours(4));
            compensationClaim5.setOvertimeEnd(LocalDateTime.now().minusDays(20));
            compensationClaim5.setClaimDateTime(LocalDateTime.now().minusDays(19));
            compensationClaim5.setOvertimeHours(4L);
            compensationClaim5.setApprovingManager(manager);
            employeeLeaveBalance.setCompensationLeave(0.5f);
            compensationClaimService.save(compensationClaim5);
            leaveBalanceService.save(employeeLeaveBalance2);

            Employee employee3 = employeeService.findByName("Mob Psycho");
            LeaveBalance employeeLeaveBalance3 = leaveBalanceService.findByEmployee(employee3.getId());

            CompensationClaim compensationClaim6 = new CompensationClaim();
            compensationClaim6.setClaimingEmployee(employee3);
            compensationClaim6.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
            compensationClaim6.setCompensationLeaveRequested(0.5f);
            compensationClaim6.setOvertimeStart(LocalDateTime.now().minusDays(6).minusHours(4));
            compensationClaim6.setOvertimeEnd(LocalDateTime.now().minusDays(6));
            compensationClaim6.setClaimDateTime(LocalDateTime.now().minusDays(4));
            compensationClaim6.setOvertimeHours(4L);
            compensationClaim6.setApprovingManager(manager);
            employeeLeaveBalance.setCompensationLeave(0.5f);
            compensationClaimService.save(compensationClaim6);
            leaveBalanceService.save(employeeLeaveBalance3);

            CompensationClaim compensationClaim7 = new CompensationClaim();
            compensationClaim7.setClaimingEmployee(employee3);
            compensationClaim7.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
            compensationClaim7.setCompensationLeaveRequested(0.5f);
            compensationClaim7.setOvertimeStart(LocalDateTime.now().minusDays(5).minusHours(4));
            compensationClaim7.setOvertimeEnd(LocalDateTime.now().minusDays(5));
            compensationClaim7.setClaimDateTime(LocalDateTime.now().minusDays(3));
            compensationClaim7.setOvertimeHours(4L);
            compensationClaim7.setApprovingManager(manager);
            employeeLeaveBalance.setCompensationLeave(0.5f);
            compensationClaimService.save(compensationClaim7);
            leaveBalanceService.save(employeeLeaveBalance3);

            System.out.println("End Compensation Claim Seeder");
        }
    }
}
