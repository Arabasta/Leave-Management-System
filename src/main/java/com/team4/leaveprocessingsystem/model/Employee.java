package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(optional = false)
    private JobDesignation jobDesignation;

    @OneToMany(mappedBy = "employee", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @ManyToOne
    private Manager manager;

    @OneToOne(orphanRemoval = true)
    private LeaveBalance leaveBalance;

    @OneToMany(mappedBy = "submittingEmployee", orphanRemoval = true)
    private List<LeaveApplication> leaveApplications;

    private String name;

    public Employee() {}

    public Employee(String name, JobDesignation jobDesignation,
                    Manager manager, LeaveBalance leaveBalance) {
        this.name = name;
        this.jobDesignation = jobDesignation;
        this.manager = manager;
        this.leaveBalance = leaveBalance;
        this.users = new ArrayList<>();
    }

    public void setManager(Manager manager) {
        // remove from old manager
        if (this.manager != null) {
            this.manager.removeSubordinate(this);
        }

        this.manager = manager;
        if (manager != null) {
            manager.addSubordinate(this);
        }
    }

    public void setUser(User user) {
        users.add(user);
    }
}
