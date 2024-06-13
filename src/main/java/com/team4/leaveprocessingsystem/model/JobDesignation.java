package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class JobDesignation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false, unique = true)
    private String name;

    // todo: verify mapping to leave type; note: Job Designation owns leave types
    @ManyToMany(fetch = FetchType.EAGER)
    /*@JoinTable(name = "job_designation_eligible_leave_types",
            joinColumns = @JoinColumn(name = "job_designation_id"),
            inverseJoinColumns = @JoinColumn(name = "leave_type_id")) */
    private List<LeaveType> eligibleLeaveTypesByJobDesignation;

    @Min(value = 0, message = "Number of leave(s) must be more than or equal to 0")
    @Column(nullable = false)
    private int defaultAnnualLeaves;

    public JobDesignation() {}


    public JobDesignation(String name, int defaultAnnualLeaves,
                          List<LeaveType> eligibleLeaveTypesByJobDesignation) {
        this.name = name;
        this.defaultAnnualLeaves = defaultAnnualLeaves;
        this.eligibleLeaveTypesByJobDesignation = eligibleLeaveTypesByJobDesignation;
    }

}
