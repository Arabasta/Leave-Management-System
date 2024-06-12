package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Manager extends Employee {
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Employee> subordinates;

    @OneToMany(mappedBy = "reviewingManager")
    private List<LeaveApplication> leaveApplications;

    @OneToMany(mappedBy = "approvingManager")
    private List<CompensationClaim> compensationClaims;

    public Manager() {}

    public Manager(String name, JobDesignation jobDesignation, Manager manager, LeaveBalance leaveBalance) {
        super(name, jobDesignation, manager, leaveBalance);
        this.subordinates = new ArrayList<>();
    }
}
