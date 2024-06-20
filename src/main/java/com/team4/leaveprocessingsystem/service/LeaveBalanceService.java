package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveBalance;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import com.team4.leaveprocessingsystem.util.DateTimeCounterUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveBalanceService implements ILeaveBalance {
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PublicHolidayService publicHolidayService;
    private final EmployeeService employeeService;
    @Autowired
    public LeaveBalanceService(LeaveBalanceRepository leaveBalanceRepository, PublicHolidayService publicHolidayService, EmployeeService employeeService) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.publicHolidayService = publicHolidayService;
        this.employeeService = employeeService;

    }

    @Override
    @Transactional
    public boolean save(LeaveBalance leaveBalance) {
        try {
            leaveBalanceRepository.save(leaveBalance);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException("leave balance");
        }
    }

    @Override
    @Transactional
    public void update(LeaveApplication leaveApplication){
        // Update employee's leave balances
        Employee employee = leaveApplication.getSubmittingEmployee();
        LeaveBalance empLeaveBalance = employee.getLeaveBalance();
        LeaveTypeEnum leaveType = leaveApplication.getLeaveType();

        Long numOfLeaveToBeCounted = DateTimeCounterUtils.numOfLeaveToBeCounted(leaveApplication.getStartDate(), leaveApplication.getEndDate(), leaveType, publicHolidayService);
        switch(leaveType){
            case MEDICAL:
                empLeaveBalance.setCurrentMedicalLeave(empLeaveBalance.getCurrentMedicalLeave() - numOfLeaveToBeCounted);
                break;
            case ANNUAL:
                empLeaveBalance.setCurrentAnnualLeave(empLeaveBalance.getCurrentAnnualLeave() - numOfLeaveToBeCounted);
                break;
            case COMPASSIONATE:
                empLeaveBalance.setCompassionateLeaveConsumed(empLeaveBalance.getCompassionateLeaveConsumed() + numOfLeaveToBeCounted);
                break;
            case COMPENSATION:
                empLeaveBalance.setCurrentCompensationLeave(empLeaveBalance.getCurrentCompensationLeave() - numOfLeaveToBeCounted);
                break;
            case UNPAID:
                empLeaveBalance.setUnpaidLeaveConsumed(empLeaveBalance.getUnpaidLeaveConsumed() + numOfLeaveToBeCounted);
                break;
        }

        leaveBalanceRepository.save(empLeaveBalance);
    }

    @Override
    @Transactional
    public void refund(LeaveApplication leaveApplication){
        // Refund an employee's leave balance when cancelled
        Employee employee = leaveApplication.getSubmittingEmployee();
        LeaveBalance empLeaveBalance = employee.getLeaveBalance();
        LeaveTypeEnum leaveType = leaveApplication.getLeaveType();
        Long numOfLeaveToBeCounted = DateTimeCounterUtils.numOfLeaveToBeCounted(leaveApplication.getStartDate(), leaveApplication.getEndDate(), leaveType, publicHolidayService);
        switch(leaveType){
            case MEDICAL:
                empLeaveBalance.setCurrentMedicalLeave(empLeaveBalance.getCurrentMedicalLeave() + numOfLeaveToBeCounted);
                break;
            case ANNUAL:
                empLeaveBalance.setCurrentAnnualLeave(empLeaveBalance.getCurrentAnnualLeave() + numOfLeaveToBeCounted);
                break;
            case COMPASSIONATE:
                empLeaveBalance.setCompassionateLeaveConsumed(empLeaveBalance.getCompassionateLeaveConsumed() - numOfLeaveToBeCounted);
                break;
            case COMPENSATION:
                empLeaveBalance.setCurrentCompensationLeave(empLeaveBalance.getCurrentCompensationLeave() + numOfLeaveToBeCounted);
                break;
            case UNPAID:
                empLeaveBalance.setUnpaidLeaveConsumed(empLeaveBalance.getUnpaidLeaveConsumed() - numOfLeaveToBeCounted);
                break;
        }

        leaveBalanceRepository.save(empLeaveBalance);
    }

    @Override
    @Transactional
    public void updateCompensationLeave(CompensationClaim claim) {
        LeaveBalance employeeLeaveBalance = claim.getClaimingEmployee().getLeaveBalance();
        employeeLeaveBalance.setCompensationLeave(employeeLeaveBalance.getCompensationLeave() + claim.getCompensationLeaveRequested());
        leaveBalanceRepository.save(employeeLeaveBalance);
    }

    @Override
    @Transactional
    public LeaveBalance findByEmployee(int employee_id) {
        return employeeService.findEmployeeById(employee_id).getLeaveBalance();
    }

    @Override
    @Transactional
    public LeaveBalance findLeaveBalanceById(int id) {
        return leaveBalanceRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public void resetLeaves() {
        List<LeaveBalance> allLeaveBalances = leaveBalanceRepository.findAll();
        for (LeaveBalance leaveBalance : allLeaveBalances) {
            // annual leaves carry over
            leaveBalance.setCurrentAnnualLeave(leaveBalance.getAnnualLeave() + leaveBalance.getCurrentAnnualLeave());

            // rest of leaves reset
            leaveBalance.setCurrentMedicalLeave(leaveBalance.getMedicalLeave());
            leaveBalance.setCompassionateLeaveConsumed(0);
            leaveBalance.setCurrentCompensationLeave(0);
            leaveBalance.setUnpaidLeaveConsumed(0);
            leaveBalanceRepository.save(leaveBalance);
        }
    }
}
