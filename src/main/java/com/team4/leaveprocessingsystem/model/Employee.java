package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Job Designation cannot be blank")
    @ManyToOne(optional = false)
    private JobDesignation jobDesignation;

    @ManyToOne
    private Manager manager;

    @OneToOne(orphanRemoval = true)
    private LeaveBalance leaveBalance;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    public Employee() {}

    public Employee(String name, JobDesignation jobDesignation,
                    Manager manager, LeaveBalance leaveBalance) {
        this.name = name;
        this.jobDesignation = jobDesignation;
        this.manager = manager;
        this.leaveBalance = leaveBalance;
    }

    // todo: test this
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

}
