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

    @Column(nullable = false)
    private float compensationLeaveRequested;

    @Column(nullable = false)
    @Past(message = "Overtime Start DateTime must be in the past")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime overtimeStartDateTime;

    @Column(nullable = false)
    @Past(message = "Overtime End DateTime must be in the past")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime overtimeEndDateTime;

    @Column(nullable = false)
    private float overtimeHours;

    @ManyToOne(optional = false)
    private Manager approvingManager;

    private LocalDateTime reviewedDateTime;

    @ManyToOne(optional = false)
    private Employee claimingEmployee;

    private String comments;

    public CompensationClaim() {
    }

    public CompensationClaim(int id) {
        this.id = id;
    }

    public CompensationClaim(Integer id, CompensationClaimStatusEnum compensationClaimStatus,
                             float compensationLeaveRequested, LocalDateTime overtimeStartDateTime,
                             LocalDateTime overtimeEndDateTime, Manager approvingManager,
                             LocalDateTime reviewedDateTime, Employee claimingEmployee,
                             String comments) {
        super();
        this.id = id;
        this.compensationClaimStatus = compensationClaimStatus;
        this.compensationLeaveRequested = compensationLeaveRequested;
        this.overtimeStartDateTime = overtimeStartDateTime;
        this.overtimeEndDateTime = overtimeEndDateTime;
        this.approvingManager = approvingManager;
        this.reviewedDateTime = reviewedDateTime;
        this.claimingEmployee = claimingEmployee;
        this.comments = comments;
    }
}
