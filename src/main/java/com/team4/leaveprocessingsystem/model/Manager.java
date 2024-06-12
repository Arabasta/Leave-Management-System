package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Manager extends Employee {

    public Manager() {}

    public Manager(String name, JobDesignation jobDesignation, Manager manager, LeaveBalance leaveBalance) {
        super(name, jobDesignation, manager, leaveBalance);
    }
}
