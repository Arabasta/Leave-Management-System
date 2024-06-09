package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CompensationClaimValidator implements Validator {

    @Override
    public boolean supports(Class<?> compensationClaim) {
        return CompensationClaim.class.isAssignableFrom(compensationClaim);
    }

    //TODO: to review implementation
    @Override
    public void validate(Object target, Errors errors) {
        CompensationClaim claim = (CompensationClaim) target;
        if ((claim.getOvertimeStartDateTime() != null && claim.getOvertimeEndDateTime() != null) &&
                (claim.getOvertimeStartDateTime().isAfter(claim.getOvertimeEndDateTime()))) {
            errors.rejectValue("toDate", "error.dates", "Overtime End date should be greater than Overtime Start date.");
        }
    }

    /* conditions to implement for validate
    @Transactional
    public boolean validateClaims(List<CompensationClaim> compensationClaims) {
        String error = null;
        if (compensationClaims.isEmpty()) {
            error = "There are no compensation claims";
        }
        for (CompensationClaim c : compensationClaims) {
            if (c.getClaimingEmployee() == null) {
                error = "Compensation claim employee is null";
                break;
            } else if (c.getApprovingManager() == null){
                error = "Compensation claim approving manager is null";
                break;
            } else if (c.getOvertimeStartDateTime().isAfter(c.getOvertimeEndDateTime())) {
                error = "Compensation claim overtime start date is after end date";
                break;
            } else if (c.getCompensationLeaveRequested() <= 0) {
                error = "Compensation claim overtime leave requested is less than 0";
                break;
            }
        }
        if (error != null)
            throw new CompensationClaimInvalidException(error);
        return true;
    }
     */
}
