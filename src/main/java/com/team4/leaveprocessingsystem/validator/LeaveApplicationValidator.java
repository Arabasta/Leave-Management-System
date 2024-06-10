package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.service.PublicHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

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
        LeaveApplication application = (LeaveApplication) target;
        LocalDate startDate = application.getStartDate();
        LocalDate endDate = application.getEndDate();

        if (startDate != null && endDate != null) {

            List<LocalDate> publicHolidayList = publicHolidayService.publicHolidayDateList();

            if (publicHolidayList.contains(startDate) ||
                    startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                errors.rejectValue("startDate", "error.dates", "Start date should be a working day");
            }

            if (publicHolidayList.contains(endDate) ||
                    endDate.getDayOfWeek() == DayOfWeek.SATURDAY || endDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                errors.rejectValue("endDate", "error.dates", "End date should be a working day");
            }

            if (startDate.isAfter(endDate)) {
                errors.rejectValue("endDate", "error.dates",
                        "End date must be later than Start Date");
            }
        }
    }
}
