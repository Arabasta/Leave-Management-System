package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LeaveApplicationSeeder {

    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;

    public LeaveApplicationSeeder(LeaveApplicationService leaveApplicationService,
                                  EmployeeService employeeService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
    }

    public void seed() {
        if (leaveApplicationService.count() == 0) {
            // todo : add a lot more and modularise
            Employee employee = employeeService.findByName("Administrative Employee");
            Employee manager = employeeService.findManagerByName("Manager");

            LeaveApplication leaveApplication1 = new LeaveApplication();
            leaveApplication1.setSubmittingEmployee(employee);
            leaveApplication1.setReviewingManager(manager);
            leaveApplication1.setLeaveStatus(LeaveStatusEnum.APPLIED);
            leaveApplication1.setLeaveType(LeaveTypeEnum.ANNUAL);
            leaveApplication1.setStartDateTime(LocalDateTime.now().plusDays(1));
            leaveApplication1.setEndDateTime(LocalDateTime.now().plusDays(15));
            leaveApplication1.setSubmissionReason("Japan trip");
            leaveApplicationService.save(leaveApplication1);

            LeaveApplication leaveApplication2 = new LeaveApplication();
            leaveApplication2.setSubmittingEmployee(employee);
            leaveApplication2.setReviewingManager(manager);
            leaveApplication2.setLeaveStatus(LeaveStatusEnum.APPLIED);
            leaveApplication2.setLeaveType(LeaveTypeEnum.MEDICAL);
            leaveApplication2.setStartDateTime(LocalDateTime.now().plusDays(10));
            leaveApplication2.setEndDateTime(LocalDateTime.now().plusDays(12));
            leaveApplication2.setSubmissionReason("broke leg");
            leaveApplicationService.save(leaveApplication2);
        }
    }
}
