package com.team4.leaveprocessingsystem.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private float annualLeave;

    @Column(nullable = false)
    private float currentAnnualLeave;

    @Column(nullable = false)
    private float medicalLeave = 60;

    @Column(nullable = false)
    private float currentMedicalLeave = medicalLeave;

    public static final float COMPASSIONATE_LEAVE = 4;

    @Column(nullable = false)
    private float compassionateLeaveConsumed = 0;

    @Column(nullable = false)
    private float compensationLeave = 0;

    @Column(nullable = false)
    private float currentCompensationLeave = compensationLeave;

    @Column(nullable = false)
    private float unpaidLeaveConsumed = 0;

    public LeaveBalance() {}

    public LeaveBalance(int annualLeave) {
        this.annualLeave = annualLeave;
        currentAnnualLeave = annualLeave;
    }
}
