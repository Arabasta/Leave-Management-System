package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime overtimeStartDateTime;

    @Column(nullable = false)
    private LocalDateTime overtimeEndDateTime;

    @ManyToOne(optional = false)
    private Manager approvingManager;

    @ManyToOne(optional = false)
    private Employee claimingEmployee;

    private String comments;

    public CompensationClaim() {}
}
