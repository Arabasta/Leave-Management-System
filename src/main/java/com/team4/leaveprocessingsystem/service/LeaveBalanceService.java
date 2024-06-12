package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveBalance;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import com.team4.leaveprocessingsystem.util.DateTimeCounterUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // todo: refactor this
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

        // For testing only. To delete
        System.out.println("Medical Leave Entitlement:" + empLeaveBalance.getMedicalLeave());
        System.out.println("Medical Leave Consumed:" + (empLeaveBalance.getMedicalLeave() - empLeaveBalance.getCurrentMedicalLeave()));
        System.out.println("Medical Leave Remaining:" + empLeaveBalance.getCurrentMedicalLeave());

        System.out.println("ANNUAL Leave Entitlement:" + empLeaveBalance.getAnnualLeave());
        System.out.println("ANNUAL Leave Consumed:" + (empLeaveBalance.getAnnualLeave() - empLeaveBalance.getCurrentAnnualLeave()));
        System.out.println("ANNUAL Leave Remaining:" + empLeaveBalance.getCurrentAnnualLeave());

        System.out.println("COMPASSIONATE Leave Entitlement:" + empLeaveBalance.getCompensationLeave());
        System.out.println("COMPASSIONATE Leave Consumed:" + empLeaveBalance.getCompassionateLeaveConsumed());

        System.out.println("COMPENSATION Leave Entitlement:" + empLeaveBalance.getCompensationLeave());
        System.out.println("COMPENSATION Leave Consumed:" + (empLeaveBalance.getCompensationLeave() - empLeaveBalance.getCurrentCompensationLeave()));
        System.out.println("COMPENSATION Leave Remaining:" + empLeaveBalance.getCurrentCompensationLeave());

        System.out.println("UNPAID Leave Consumed:" + empLeaveBalance.getUnpaidLeaveConsumed());
        // For testing only. To delete
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
}
