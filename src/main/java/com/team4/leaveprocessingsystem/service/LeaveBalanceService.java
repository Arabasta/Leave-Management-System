package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.Exceptions.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveBalance;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
public class LeaveBalanceService implements ILeaveBalance {
    @Autowired
    LeaveBalanceRepository leaveBalanceRepository;

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

    public LeaveBalance findByEmployee(int employee_id) {
        return employeeService.findEmployeeById(employee_id).getLeaveBalance();
    }
}
