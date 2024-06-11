package com.team4.leaveprocessingsystem.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
// This class is used as a DTO during Manager's approval of CompensationClaim (from view -> controller)
public class CompensationClaimApproval {

    private String decision;

    private String comment;

    public CompensationClaimApproval() {}

    public CompensationClaimApproval(String decision, String comment) {
        this.decision = decision;
    }

    @Override
    public String toString() {
        return "CompensationClaimApproval [decision=" + decision + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompensationClaimApproval other = (CompensationClaimApproval) obj;
        return Objects.equals(decision, other.decision);
    }

}
