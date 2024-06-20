package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.PublicHolidayService;
import com.team4.leaveprocessingsystem.util.DateTimeCounterUtils;
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
    private final PublicHolidayService publicHolidayService;
    private List<LeaveTypeEnum> leaveTypes;
    private LeaveStatusEnum[] leaveStatuses;

    public LeaveApplicationSeeder(LeaveApplicationService leaveApplicationService, EmployeeService employeeService,
                                  LeaveBalanceService leaveBalanceService, PublicHolidayService publicHolidayService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.random = new Random(42);
        this.leaveTypes = new ArrayList<>(Arrays.asList(LeaveTypeEnum.values()));
        leaveTypes.remove(LeaveTypeEnum.COMPENSATION);
        this.leaveStatuses = LeaveStatusEnum.values();
        this.leaveBalanceService = leaveBalanceService;
        this.publicHolidayService = publicHolidayService;
    }

    // Note: doesn't validate available balance against leave that are pending approval
    public void seed() {
        if (leaveApplicationService.count() == 0) {
            List<LocalDate> publicHolidays = publicHolidayService.publicHolidayDateList();
            List<LocalDate> dateList = new ArrayList<>();
            int numOfLeaves = 50; // number of leave applications to be generated per employee
            int durationBound = 3; // max duration of leave applications

            LocalDate dateToAdd = getWorkingDay(LocalDate.now(), publicHolidays);

            // Generate a list of start and end dates for the number of leave applications
            dateList.add(dateToAdd);
            dateList.add(getWorkingDay(dateToAdd.plusDays(random.nextInt(durationBound)), publicHolidays));
            for (int i = 0; i < numOfLeaves; i++){
                LocalDate startDate = getWorkingDay(dateList.get(dateList.size() - 1).plusDays(random.nextInt(durationBound) + 1), publicHolidays);
                LocalDate endDate = getWorkingDay(startDate.plusDays(random.nextInt(durationBound)), publicHolidays);
                dateList.add(startDate);
                dateList.add(endDate);
            }

            // Adds all the generated leave applications to the employee
            List<Employee> employeeList = employeeService.findAll();
            for (Employee employee : employeeList) {
                for (int i = 0; i < dateList.size(); i = i + 2){
                    leaveApplicationSeed(employee, dateList.get(i), dateList.get(i + 1),"test");
                }
            }
        }
    }

    // Given a date, gets the next possible working day
    private LocalDate getWorkingDay(LocalDate date, List<LocalDate> publicHolidays){
        while (DateTimeCounterUtils.isWeekend(date) || publicHolidays.contains(date)) {
            date = date.plusDays(1);
        }
        return date;
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

        Long numOfLeaveRequired = DateTimeCounterUtils.numOfLeaveToBeCounted(startDate, endDate, leaveApplication.getLeaveType(), publicHolidayService);
        LeaveBalance empLeaveBalance = leaveBalanceService.findByEmployee(employee.getId());
        float numOfLeaveAvailable = 0f;
        switch (leaveApplication.getLeaveType()) {
            case MEDICAL:
                numOfLeaveAvailable = empLeaveBalance.getCurrentMedicalLeave();
                if (numOfLeaveAvailable < numOfLeaveRequired) { return; }
                break;
            case ANNUAL:
                numOfLeaveAvailable = empLeaveBalance.getCurrentAnnualLeave();
                if (numOfLeaveAvailable < numOfLeaveRequired) { return; }
                break;
            case COMPENSATION:
                numOfLeaveAvailable = empLeaveBalance.getCurrentCompensationLeave();
                if (numOfLeaveAvailable < numOfLeaveRequired) { return; }
                break;
        }

        leaveApplicationService.save(leaveApplication);
        if (leaveApplication.getLeaveStatus() == LeaveStatusEnum.APPROVED) {
            leaveBalanceService.update(leaveApplication);
        }
    }
}
