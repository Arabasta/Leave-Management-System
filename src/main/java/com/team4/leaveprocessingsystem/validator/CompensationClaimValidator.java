package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class CompensationClaimValidator implements Validator {

    @Override
    public boolean supports(Class<?> clasz) {
        return CompensationClaim.class.isAssignableFrom(clasz);
    }

    //TODO: to review implementation
    @Override
    public void validate(Object target, Errors errors) {
        CompensationClaim claim = (CompensationClaim) target;
        CompensationClaimStatusEnum statusEnum = claim.getCompensationClaimStatus();
        String comments = claim.getComments();
        LocalDateTime overtimeStartDateTime = claim.getOvertimeStartDateTime();
        LocalDateTime overtimeEndDateTime = claim.getOvertimeEndDateTime();
        LocalDateTime claimDate = claim.getClaimedDateTime();

        // Ensure overtimeStartDateTime date is not empty
        if (overtimeStartDateTime ==null) {
            errors.rejectValue("overtimeStartDateTime", "error.compensationClaim.dates.1",
                    "Start DateTime cannot be empty.");
        }

        // Ensure end date is not empty
        if (overtimeEndDateTime==null) {
            errors.rejectValue("overtimeEndDateTime", "error.compensationClaim.dates.2",
                    "End DateTime cannot be empty.");
        }

        if (overtimeStartDateTime !=null && overtimeEndDateTime!=null) {

            // Ensure end date is before claim date
            if (claimDate.isBefore(overtimeEndDateTime)) {
                errors.rejectValue("overtimeEndDateTime", "error.compensationClaim.dates.3",
                        "End DateTime must be earlier than current DateTime.");
            }

            // Ensure end date is after overtimeStartDateTime date
            if (overtimeStartDateTime.isAfter(overtimeEndDateTime)) {
                errors.rejectValue("overtimeEndDateTime", "error.compensationClaim.dates.4",
                        "End DateTime must be later than Start DateTime.");
            }

            // Ensure end date is minimally equal or 4 hours after overtimeStartDateTime date
            if (Duration.between(overtimeStartDateTime, overtimeEndDateTime).toHours() < 4) {
                errors.rejectValue("overtimeEndDateTime", "error.compensationClaim.dates.5",
                        "End DateTime must be at least 4 hours after Start DateTime.");
            }

            // Ensure claim is either approved or rejected.
            if (statusEnum == null) {
                errors.rejectValue("compensationClaimStatus", "error.compensationClaim.compensationClaimStatusEnum.1",
                        "Must select Approve or Reject.");
            }

            assert statusEnum != null;
            // Ensure claim is not at APPLIED nor UPDATED state.
            if (statusEnum != CompensationClaimStatusEnum.APPLIED && statusEnum != CompensationClaimStatusEnum.UPDATED) {

                // Ensure rejection comment is valid if Manager rejects claim.
                if (statusEnum == CompensationClaimStatusEnum.REJECTED && comments.isEmpty()) {
                    errors.rejectValue("comments", "error.compensationClaim.comments.1",
                            "Must include reason for Rejection.");
                }
            }
        }
    }
}
