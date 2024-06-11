package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.PublicHolidayService;
import com.team4.leaveprocessingsystem.util.DateTimeCounterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LeaveApplicationValidator implements Validator {
    @Autowired
    private PublicHolidayService publicHolidayService;

    @Override
    public boolean supports(Class<?> clazz) {
        return LeaveApplication.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LeaveApplication leaveApplication = (LeaveApplication) target;
        LocalDate startDate = leaveApplication.getStartDate();
        LocalDate endDate = leaveApplication.getEndDate();

        if (startDate != null && endDate != null) {

            List<LocalDate> publicHolidayList = publicHolidayService.publicHolidayDateList();

            // Ensure start date is a working day
            if (publicHolidayList.contains(startDate) ||
                    startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                errors.rejectValue("startDate", "error.dates", "Start date should be a working day");
            }

            // Ensure end date is a working day
            if (publicHolidayList.contains(endDate) ||
                    endDate.getDayOfWeek() == DayOfWeek.SATURDAY || endDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                errors.rejectValue("endDate", "error.dates", "End date should be a working day");
            }

            // Ensure end date is after start date
            if (startDate.isAfter(endDate)) {
                errors.rejectValue("endDate", "error.dates",
                        "End date must be later than Start Date");
            }

            // Ensure sufficient leave balance for applied duration
            LeaveBalance empLeaveBalance = leaveApplication.getSubmittingEmployee().getLeaveBalance();
            LeaveTypeEnum leaveType = leaveApplication.getLeaveType();
            Long numOfLeaveToBeCounted = DateTimeCounterUtils.numOfLeaveToBeCounted(startDate, endDate, leaveType, publicHolidayService);

            // Get number of leave days pending approval (leave application status is APPLIED or UPDATED)
            Employee employee = leaveApplication.getSubmittingEmployee();
            List<LeaveApplication> allLeaveApplications = employee.getLeaveApplications();
            Map<Object, Object> pendingLeaveMap = allLeaveApplications.stream()
                    .filter(x -> (x.getLeaveStatus() == LeaveStatusEnum.APPLIED || x.getLeaveStatus() == LeaveStatusEnum.UPDATED))
                    .collect(Collectors.toMap(x -> x.getLeaveType(), x -> DateTimeCounterUtils.numOfLeaveToBeCounted( x.getStartDate(), x.getEndDate(), x.getLeaveType(), publicHolidayService),
                            (existingValue, newValue) -> (Long) existingValue + (Long) newValue));

            switch(leaveType){
                case MEDICAL:
                    if (empLeaveBalance.getCurrentMedicalLeave() < (numOfLeaveToBeCounted + (Long)pendingLeaveMap.get(leaveType)))
                        errors.rejectValue("endDate", "error.dates", "Duration of leave applied cannot be more than available leave balance including leave days pending approval");
                    break;
                case ANNUAL:
                    if (empLeaveBalance.getCurrentAnnualLeave() < (numOfLeaveToBeCounted + (Long)pendingLeaveMap.get(leaveType)))
                        errors.rejectValue("endDate", "error.dates", "Duration of leave applied cannot be more than available leave balance including leave days pending approval");
                    break;
                case COMPASSIONATE:
                    if (empLeaveBalance.COMPASSIONATE_LEAVE < (numOfLeaveToBeCounted + (Long)pendingLeaveMap.get(leaveType)))
                        errors.rejectValue("endDate", "error.dates", "Duration of leave applied cannot be more than available leave balance including leave days pending approval");
                    break;
                case COMPENSATION:
                    if (empLeaveBalance.getCurrentCompensationLeave() < (numOfLeaveToBeCounted + (Long)pendingLeaveMap.get(leaveType)))
                        errors.rejectValue("endDate", "error.dates", "Duration of leave applied cannot be more than available leave balance including leave days pending approval");
                    break;
            }
        }
    }
}
