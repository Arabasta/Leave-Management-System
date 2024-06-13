package com.team4.leaveprocessingsystem.model;

import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Data
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private LeaveTypeEnum leaveType;

    @Column(length = 6555)
    private String description;

    /*
    @Column(length = 65555)
    private List<LeaveLengthEnum> entitlement;
    */

    // todo: verify mapping to job designation
    //@ManyToMany (mappedBy = "eligibleLeaveTypesByJobDesignation")
    //private List<JobDesignation> eligibleJobDesignationsForLeaveType = new ArrayList<>();

    public LeaveType() {
    }

    public LeaveType(int id) {
        this.id = id;
    }

    public LeaveType(LeaveTypeEnum leaveType, String description,
                     List<JobDesignation> eligibleJobDesignationsForLeaveType) {
        this.leaveType = leaveType;
        this.description = description;
        //this.eligibleJobDesignationsForLeaveType = eligibleJobDesignationsForLeaveType;

    }

}
