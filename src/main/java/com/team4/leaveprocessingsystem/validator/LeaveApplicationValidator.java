package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.LeaveApplication;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LeaveApplicationValidator implements Validator {
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
