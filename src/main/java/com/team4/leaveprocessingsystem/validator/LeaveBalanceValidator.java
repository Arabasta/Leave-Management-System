package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.LeaveBalance;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component

public class LeaveBalanceValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return LeaveBalance.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        LeaveBalance leaveBalance = (LeaveBalance) target;

        if (leaveBalance.getAnnualLeave() < 0) {
            errors.rejectValue("annualLeave", "annualLeave.invalid", "Annual leave cannot be negative");
        }

        if (leaveBalance.getCurrentAnnualLeave() < 0 || leaveBalance.getCurrentAnnualLeave() > leaveBalance.getAnnualLeave()) {
            errors.rejectValue("currentAnnualLeave", "currentAnnualLeave.invalid",
                    "Current annual leave must be between 0 and annual leave");
        }

        if (leaveBalance.getMedicalLeave() < 0) {
            errors.rejectValue("medicalLeave", "medicalLeave.invalid", "Medical leave cannot be negative");
        }

        if (leaveBalance.getCurrentMedicalLeave() < 0 || leaveBalance.getCurrentMedicalLeave() > leaveBalance.getMedicalLeave()) {
            errors.rejectValue("currentMedicalLeave", "currentMedicalLeave.invalid",
                    "Current medical leave must be between 0 and medical leave");
        }

        if (leaveBalance.getCompassionateLeaveConsumed() < 0 || leaveBalance.getCompassionateLeaveConsumed() > LeaveBalance.COMPASSIONATE_LEAVE) {
            errors.rejectValue("compassionateLeaveConsumed", "compassionateLeaveConsumed.invalid",
                    "Compassionate leave consumed must be between 0 and the maximum allowed compassionate leave");
        }

        if (leaveBalance.getCompensationLeave() < 0) {
            errors.rejectValue("compensationLeave", "compensationLeave.invalid",
                    "Compensation leave cannot be negative");
        }

        if (leaveBalance.getCurrentCompensationLeave() < 0 || leaveBalance.getCurrentCompensationLeave() > leaveBalance.getCompensationLeave()) {
            errors.rejectValue("currentCompensationLeave", "currentCompensationLeave.invalid",
                    "Current compensation leave must be between 0 and compensation leave");
        }

        if (leaveBalance.getUnpaidLeaveConsumed() < 0) {
            errors.rejectValue("unpaidLeaveConsumed", "unpaidLeaveConsumed.invalid",
                    "Unpaid leave consumed cannot be negative");
        }
    }
}
