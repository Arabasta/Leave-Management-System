package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class JobDesignationValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return JobDesignation.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        JobDesignation jobDesignation = (JobDesignation) target;

        if (jobDesignation.getName() == null || jobDesignation.getName().isBlank()) {
            errors.rejectValue("name", "name.invalid",
                    "Name cannot be empty");
        }

        if (jobDesignation.getDefaultAnnualLeaves() < 0) {
            errors.rejectValue("defaultAnnualLeaves", "defaultannualleaves.invalid",
                    "Default annual leaves must be more than or equal to 0");
        }
    }
}
