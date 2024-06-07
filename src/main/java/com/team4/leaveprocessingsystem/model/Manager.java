package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
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

    public Manager() {}

    public Manager(String name, JobDesignation jobDesignation, Manager manager, LeaveBalance leaveBalance) {
        super(name, jobDesignation, manager, leaveBalance);
        this.subordinates = new ArrayList<>();
    }

    public void addSubordinate(Employee employee) {
        if (!subordinates.contains(employee)) {
            subordinates.add(employee);
            employee.setManager(this);
        }
    }
}
