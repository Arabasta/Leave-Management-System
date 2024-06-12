package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class CompensationClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompensationClaimStatusEnum compensationClaimStatus;

    @ManyToOne(optional = false)
    private Employee claimingEmployee;

    @Column(nullable = false)
    private LocalDateTime claimDateTime;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime overtimeStartDateTime;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime overtimeEndDateTime;

    @Column(nullable = false)
    private float overtimeHours;

    @Column(nullable = false)
    private float compensationLeaveRequested;

    @ManyToOne(optional = false)
    private Manager approvingManager;

    private LocalDateTime reviewedDateTime;

    private String comments;

    public CompensationClaim() {
    }

    @Override
    public String toString() {
        return "Compensation Claim [Id: " + id + " ], "
                + "[Status: " + compensationClaimStatus + " ], "
                + "[Claiming Employee: " + claimingEmployee + " ], "
                + "[Claim Date: " + claimDateTime + " ], "
                + "[Overtime Start DateTime: " + overtimeStartDateTime + " ], "
                + "[Overtime End DateTime: " + overtimeEndDateTime + " ], "
                + "[Overtime Hours: " + overtimeHours + " ], "
                + "[Compensation Leave Requested: " + compensationLeaveRequested + " ], "
                + "[Approving Manager: " + approvingManager + " ], "
                + "[Reviewed Date: " + reviewedDateTime + " ], "
                + "[Comments: " + comments + " ], ";
    }
}
