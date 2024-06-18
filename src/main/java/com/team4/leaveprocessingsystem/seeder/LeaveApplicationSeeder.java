package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class LeaveApplicationSeeder {

    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;
    private final Random random;
    private final LeaveBalanceService leaveBalanceService;
    private List<LeaveTypeEnum> leaveTypes;
    private LeaveStatusEnum[] leaveStatuses;

    public LeaveApplicationSeeder(LeaveApplicationService leaveApplicationService,
                                  EmployeeService employeeService, LeaveBalanceService leaveBalanceService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.random = new Random(42);
        this.leaveTypes = new ArrayList<>(Arrays.asList(LeaveTypeEnum.values()));
        leaveTypes.remove(LeaveTypeEnum.COMPENSATION);
        this.leaveStatuses = LeaveStatusEnum.values();
        this.leaveBalanceService = leaveBalanceService;
    }

    // Note: bypasses working days and entitlement validation
    public void seed() {
        if (leaveApplicationService.count() == 0) {

            List<LocalDate> dateList = new ArrayList<>();
            int numOfLeaves = 3;
            int durationBound = 3;
            dateList.add(LocalDate.now());
            dateList.add(LocalDate.now().plusDays(random.nextInt(durationBound)));
            for (int i = 0; i < numOfLeaves; i++){
                LocalDate startDate = dateList.get(dateList.size() - 1).plusDays(random.nextInt(durationBound) + 1);
                LocalDate endDate = startDate.plusDays(random.nextInt(durationBound));
                dateList.add(startDate);
                dateList.add(endDate);
            }

            List<Employee> employeeList = employeeService.findAll();
            for (Employee employee : employeeList) {
                for (int i = 0; i < dateList.size(); i = i + 2){
                    leaveApplicationSeed(employee, dateList.get(i), dateList.get(i + 1),"test");
                }
            }
        }
    }

    private void leaveApplicationSeed(Employee employee, LocalDate startDate, LocalDate endDate, String reason) {
        Manager manager = employee.getManager();

        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(manager);
        leaveApplication.setLeaveStatus(leaveStatuses[random.nextInt(leaveStatuses.length)]);
        leaveApplication.setLeaveType(leaveTypes.get(random.nextInt(leaveTypes.size())));
        leaveApplication.setStartDate(startDate);
        leaveApplication.setEndDate(endDate);
        leaveApplication.setSubmissionReason(reason);
        leaveApplicationService.save(leaveApplication);
        if (leaveApplication.getLeaveStatus() == LeaveStatusEnum.APPROVED) {
            leaveBalanceService.update(leaveApplication);
        }
    }
}
