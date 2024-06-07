package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    private Role role;

    @OneToOne(orphanRemoval = true)
    private User user;

    @ManyToOne
    private Manager manager;

    @OneToOne(orphanRemoval = true)
    private LeaveBalance leaveBalance;

    @OneToMany(mappedBy = "submittingEmployee", orphanRemoval = true)
    private List<LeaveApplication> leaveApplications;

    private String name;

    public Employee() {}

    public Employee(String name, Role role,
                    Manager manager, LeaveBalance leaveBalance) {
        this.name = name;
        this.role = role;
        this.manager = manager;
        this.leaveBalance = leaveBalance;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
        if (manager != null) {
            manager.addSubordinate(this);
        }
    }
}
