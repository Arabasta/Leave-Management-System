package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CompensationClaimValidator implements Validator {

    private final CompensationClaimService compensationClaimService;

    CompensationClaimValidator(CompensationClaimService compensationClaimService) {
        this.compensationClaimService = compensationClaimService;
    }

    @Override
    public boolean supports(Class<?> clasz) {
        return CompensationClaim.class.isAssignableFrom(clasz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CompensationClaim claim = (CompensationClaim) target;
        CompensationClaimStatusEnum claimStatus = claim.getClaimStatus();
        String comments = claim.getComments();
        LocalDateTime overtimeStart = claim.getOvertimeStart();
        LocalDateTime overtimeEnd = claim.getOvertimeEnd();
        LocalDateTime claimDate = claim.getClaimDateTime();

        // Ensure overtimeStart date is not empty
        if (overtimeStart == null) {
            errors.rejectValue("overtimeStart", "error.compensationClaim.dates.1",
                    "Start DateTime cannot be empty.");
        }

        // Ensure end date is not empty
        if (overtimeEnd == null) {
            errors.rejectValue("overtimeEnd", "error.compensationClaim.dates.2",
                    "End DateTime cannot be empty.");
        }

        if (overtimeStart !=null && overtimeEnd !=null) {

            if (compensationClaimService.isClashWithExisting(claim)) {
                errors.rejectValue("overtimeStart", "error.compensationClaim.dates.3",
                        "Overtime entered overlaps with existing compensation claims.");
                errors.rejectValue("overtimeEnd", "error.compensationClaim.dates.4",
                        "Overtime entered overlaps with existing compensation claims.");
            }

            // Ensure end date is before claim date
            if (claimDate.isBefore(overtimeEnd)) {
                errors.rejectValue("overtimeEnd", "error.compensationClaim.dates.5",
                        "End DateTime must be earlier than current DateTime.");
            }

            // Ensure end date is minimally equal or 4 hours after overtimeStart date
            if (Duration.between(overtimeStart, overtimeEnd).toHours() < 4) {
                errors.rejectValue("overtimeEnd", "error.compensationClaim.dates.6",
                        "End DateTime must be at least 4 hours after Start DateTime.");
            }

            // Ensure claim is either approved or rejected.
            if (claimStatus == null) {
                errors.rejectValue("claimStatus", "error.compensationClaim.claimStatusEnum.1",
                        "Must select Approve or Reject.");
            }

            assert claimStatus != null;
            // Ensure claim is not at APPLIED nor UPDATED state.
            if (claimStatus != CompensationClaimStatusEnum.APPLIED && claimStatus != CompensationClaimStatusEnum.UPDATED) {

                // Ensure rejection comment is valid if Manager rejects claim.
                if (claimStatus == CompensationClaimStatusEnum.REJECTED && comments.isEmpty()) {
                    errors.rejectValue("comments", "error.compensationClaim.comments.1",
                            "Must include reason for Rejection.");
                }
            }
        }
    }
}
