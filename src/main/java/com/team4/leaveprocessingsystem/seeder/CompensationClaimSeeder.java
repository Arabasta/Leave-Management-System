package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.service.repo.CompensationClaimService;
import com.team4.leaveprocessingsystem.service.repo.EmployeeService;
import com.team4.leaveprocessingsystem.service.repo.LeaveBalanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CompensationClaimSeeder {

    private final CompensationClaimService compensationClaimService;
    private final EmployeeService employeeService;
    private final LeaveBalanceService leaveBalanceService;
    private final EmployeeRepository employeeRepository;
    private final Random random;
    private CompensationClaimStatusEnum[] claimStatusEnum;

    public CompensationClaimSeeder(CompensationClaimService compensationClaimService,
                                   EmployeeService employeeService,
                                   LeaveBalanceService leaveBalanceService, EmployeeRepository employeeRepository) {
        this.compensationClaimService = compensationClaimService;
        this.employeeService = employeeService;
        this.leaveBalanceService = leaveBalanceService;
        this.employeeRepository = employeeRepository;
        this.random = new Random(42);
        this.claimStatusEnum = CompensationClaimStatusEnum.values();
    }

    public void seed() {
        if (compensationClaimService.count() == 0) {
            System.out.println("Start Compensation Claim Seeder");
            generatedSeed();
            manualSeed();
            System.out.println("End Compensation Claim Seeder");
        }
    }

    private void generatedSeed() {
        Map<Integer, ArrayList<LocalDateTime>> claims = new HashMap<>();
        int numOfClaims = 3;
        for (int i = 0; i < numOfClaims; i++){
            ArrayList<LocalDateTime> claimDates = new ArrayList<>();
            claimDates.add(LocalDateTime.now().minusMonths(6).minusHours(3));
            claimDates.add(claimDates.get(0).plusHours(3));
            claimDates.add(claimDates.get(1).plusDays(1).plusHours(3));
            claims.put(i, claimDates);
        }
        int counter = 0;
        List<Employee> employeeList = employeeService.findAll();
        for (Employee employee : employeeList) {
            for (var entry : claims.entrySet()){
                ArrayList<LocalDateTime> dates = entry.getValue();
                claimSeed(employee, claimStatusEnum[counter % 6], dates.get(0), dates.get(1), dates.get(2));
                counter++;
            }
        }
    }

    private void claimSeed(Employee employee, CompensationClaimStatusEnum claimStatusEnum, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, LocalDateTime claimDateTime) {
        Manager manager = employee.getManager();
        if (manager == null) {return;} // short-circuit seeding if employee is not reporting to anyone.
        CompensationClaim claim = new CompensationClaim();
        claim.setClaimStatus(claimStatusEnum);
        claim.setClaimingEmployee(employee);
        claim.setClaimDateTime(claimDateTime);
        claim.setOvertimeStart(startDateTime);
        claim.setOvertimeEnd(endDateTime);
        claim.setOvertimeHours(compensationClaimService.calculateOvertimeHours(claim));
        claim.setCompensationLeaveRequested(compensationClaimService.calculateLeaveRequested(claim));
        claim.setApprovingManager(manager);
        if (claim.getClaimStatus() == CompensationClaimStatusEnum.APPROVED
                || claim.getClaimStatus() == CompensationClaimStatusEnum.REJECTED) {
            claim.setReviewedDateTime(claimDateTime.plusDays(3));
        }
        if (claim.getClaimStatus() == CompensationClaimStatusEnum.REJECTED) {
            claim.setComments("You came to work late.");
        }
        compensationClaimService.save(claim);
    }

    private void manualSeed() { //stable test user+password accounts: (manager,manager) and (employee,employee)
        Employee employee = employeeService.findByName("Mikasa Ackerman");
        LeaveBalance employeeLeaveBalance = leaveBalanceService.findByEmployee(employee.getId());

        CompensationClaim claim1 = new CompensationClaim();
        claim1.setClaimStatus(CompensationClaimStatusEnum.APPROVED);
        claim1.setClaimingEmployee(employee);
        claim1.setOvertimeStart(LocalDateTime.now().minusDays(20).minusHours(4));
        claim1.setOvertimeEnd(LocalDateTime.now().minusDays(20));
        claim1.setOvertimeHours(compensationClaimService.calculateOvertimeHours(claim1));
        claim1.setCompensationLeaveRequested(compensationClaimService.calculateLeaveRequested(claim1));
        claim1.setClaimDateTime(LocalDateTime.now().minusDays(19));
        claim1.setApprovingManager(employee.getManager());
        claim1.setReviewedDateTime(LocalDateTime.now().minusDays(19).minusHours(3));
        employeeLeaveBalance.setCompensationLeave(compensationClaimService.calculateLeaveRequested(claim1));
        leaveBalanceService.save(employeeLeaveBalance);
        compensationClaimService.save(claim1);

        CompensationClaim claim2 = new CompensationClaim();
        claim2.setClaimStatus(CompensationClaimStatusEnum.REJECTED);
        claim2.setClaimingEmployee(employee);
        claim2.setOvertimeStart(LocalDateTime.now().minusDays(18).minusHours(4));
        claim2.setOvertimeEnd(LocalDateTime.now().minusDays(18));
        claim2.setOvertimeHours(compensationClaimService.calculateOvertimeHours(claim2));
        claim2.setCompensationLeaveRequested(compensationClaimService.calculateLeaveRequested(claim2));
        claim2.setClaimDateTime(LocalDateTime.now().minusDays(17));
        claim2.setApprovingManager(employee.getManager());
        claim2.setReviewedDateTime(LocalDateTime.now().minusDays(17).minusHours(1));
        claim2.setComments("You went for 3 hours lunch. Your Overtime work was only 2 hours.");
        compensationClaimService.save(claim2);

        CompensationClaim claim3 = new CompensationClaim();
        claim3.setClaimingEmployee(employee);
        claim3.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
        claim3.setCompensationLeaveRequested(1.0f);
        claim3.setOvertimeStart(LocalDateTime.now().minusDays(15).minusHours(8));
        claim3.setOvertimeEnd(LocalDateTime.now().minusDays(15));
        claim3.setClaimDateTime(LocalDateTime.now().minusDays(14));
        claim3.setOvertimeHours(8L);
        claim3.setApprovingManager(employee.getManager());
        employeeLeaveBalance.setCompensationLeave(1.0f);
        compensationClaimService.save(claim3);

        CompensationClaim claim4 = new CompensationClaim();
        claim4.setClaimingEmployee(employee);
        claim4.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
        claim4.setCompensationLeaveRequested(1.0f);
        claim4.setOvertimeStart(LocalDateTime.now().minusDays(13).minusHours(8));
        claim4.setOvertimeEnd(LocalDateTime.now().minusDays(13));
        claim4.setClaimDateTime(LocalDateTime.now().minusDays(12));
        claim4.setOvertimeHours(8L);
        claim4.setApprovingManager(employee.getManager());
        employeeLeaveBalance.setCompensationLeave(1.0f);
        compensationClaimService.save(claim4);

        Employee employee2 = employeeService.findByName("Andrew");

        CompensationClaim claim5 = new CompensationClaim();
        claim5.setClaimingEmployee(employee2);
        claim5.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
        claim5.setCompensationLeaveRequested(0.5f);
        claim5.setOvertimeStart(LocalDateTime.now().minusDays(20).minusHours(4));
        claim5.setOvertimeEnd(LocalDateTime.now().minusDays(20));
        claim5.setClaimDateTime(LocalDateTime.now().minusDays(19));
        claim5.setOvertimeHours(4L);
        claim5.setApprovingManager(employee2.getManager());
        employeeLeaveBalance.setCompensationLeave(0.5f);
        compensationClaimService.save(claim5);

        CompensationClaim claim6 = new CompensationClaim();
        claim6.setClaimingEmployee(employee2);
        claim6.setClaimStatus(CompensationClaimStatusEnum.APPLIED);
        claim6.setCompensationLeaveRequested(0.5f);
        claim6.setOvertimeStart(LocalDateTime.now().minusDays(6).minusHours(4));
        claim6.setOvertimeEnd(LocalDateTime.now().minusDays(6));
        claim6.setClaimDateTime(LocalDateTime.now().minusDays(4));
        claim6.setOvertimeHours(4L);
        claim6.setApprovingManager(employee2.getManager());
        employeeLeaveBalance.setCompensationLeave(0.5f);
        compensationClaimService.save(claim6);
    }
}
