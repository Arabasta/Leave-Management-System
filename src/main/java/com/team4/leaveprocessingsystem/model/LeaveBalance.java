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
    private int annualLeave;

    @Column(nullable = false)
    private static final int MEDICAL_LEAVE = 60;

    public LeaveBalance() {}

    public LeaveBalance(int annualLeave) {

        this.annualLeave = annualLeave;
    }
}
