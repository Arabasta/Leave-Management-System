package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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
    @JoinColumn(name = "reviewing_manager_id")
    private Manager reviewingManager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatusEnum leaveStatus;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Leave type cannot be blank")
    @Column(nullable = false)
    private LeaveTypeEnum leaveType;

    @NotNull(message = "Start date cannot be blank")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be blank")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "Reason cannot be blank")
    @Column(nullable = false)
    private String submissionReason;

    private String rejectionReason;

    private String workDissemination;

    private String contactDetails;

    public LeaveApplication() {}
}
