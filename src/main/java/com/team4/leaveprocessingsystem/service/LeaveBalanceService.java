package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveBalance;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import com.team4.leaveprocessingsystem.utility.DaysCounterUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class LeaveBalanceService implements ILeaveBalance {
    @Autowired
    LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    PublicHolidayService publicHolidayService;
    @Autowired
    EmployeeService employeeService;

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

        Long numOfCalendarDays = DaysCounterUtils.countCalendarDays(leaveApplication.getStartDate(), leaveApplication.getEndDate(), publicHolidayService);
        Long numOfWorkingDays = DaysCounterUtils.countWorkingDays(leaveApplication.getStartDate(), leaveApplication.getEndDate(), publicHolidayService);
        Long numOfAnnualLeaveDeducted;
        if (numOfCalendarDays <= 14) {
            numOfAnnualLeaveDeducted = numOfWorkingDays;
        }
        else {
            numOfAnnualLeaveDeducted = numOfCalendarDays;
        }

        switch(leaveType){
            case MEDICAL:
                empLeaveBalance.setCurrentMedicalLeave(empLeaveBalance.getCurrentMedicalLeave() - numOfWorkingDays);
                break;
            case ANNUAL:
                empLeaveBalance.setCurrentAnnualLeave(empLeaveBalance.getCurrentAnnualLeave() - numOfAnnualLeaveDeducted);
                break;
            case COMPASSIONATE:
                empLeaveBalance.setCompassionateLeaveConsumed(empLeaveBalance.getCompassionateLeaveConsumed() + numOfWorkingDays);
                break;
            case COMPENSATION:
                empLeaveBalance.setCurrentCompensationLeave(empLeaveBalance.getCurrentCompensationLeave() - numOfWorkingDays);
                break;
            case UNPAID:
                empLeaveBalance.setUnpaidLeaveConsumed(empLeaveBalance.getUnpaidLeaveConsumed() + numOfWorkingDays);
                break;
        }

        leaveBalanceRepository.save(empLeaveBalance);

        // For testing only. To delete
        System.out.println(numOfWorkingDays);
        System.out.println(numOfCalendarDays);
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
}
