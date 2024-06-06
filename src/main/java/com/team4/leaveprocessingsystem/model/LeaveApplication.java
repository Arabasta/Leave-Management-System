package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    private Employee submittingEmployee;

    @ManyToOne
    private Employee reviewingManager;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    private LeaveStatusEnum leaveStatus;

    @Column(nullable = false)
    private LeaveTypeEnum leaveType;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String submissionReason;

    private String rejectionReason;

    public LeaveApplication() {}
}
