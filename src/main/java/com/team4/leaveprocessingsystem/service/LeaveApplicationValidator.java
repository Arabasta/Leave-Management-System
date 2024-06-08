package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.interfacemethods.IValidator;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import org.springframework.validation.Errors;

public class LeaveApplicationValidator implements IValidator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LeaveApplication.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LeaveApplication application = (LeaveApplication) target;
        if ((application.getStartDateTime() != null) && (application.getEndDateTime() != null) &&
                (application.getStartDateTime().isAfter(application.getEndDateTime()))) {
            errors.rejectValue("endDate", "error.dates",
                    "End Date must be later than Start Date");
        }
    }
}
