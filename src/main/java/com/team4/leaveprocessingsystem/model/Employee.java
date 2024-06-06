package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Employee extends User {
    @ManyToOne(optional = false)
    private Role role;

    @ManyToOne
    private Manager manager;

    @OneToOne(cascade = CascadeType.ALL)
    private LeaveBalance leaveBalance;

    @OneToMany(mappedBy = "submittingEmployee")
    private List<LeaveApplication> leaveApplications;

    private String name;

    public Employee() {}
}
