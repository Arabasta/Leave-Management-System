package com.team4.leaveprocessingsystem.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)
    private float annualLeave;

    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)
    private float currentAnnualLeave;

    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)
    private float medicalLeave = 60;

    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)
    private float currentMedicalLeave = medicalLeave;

    public static final float COMPASSIONATE_LEAVE = 4;

    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)
    private float compassionateLeaveConsumed = 0;

    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)
    private float compensationLeave = 0;

    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)

    private float currentCompensationLeave = compensationLeave;
    @Min(value = 0, message = "Cannot be negative")
    @Column(nullable = false)
    private float unpaidLeaveConsumed = 0;

    public LeaveBalance() {}

    public LeaveBalance(int annualLeave) {
        this.annualLeave = annualLeave;
        currentAnnualLeave = annualLeave;
    }
}
