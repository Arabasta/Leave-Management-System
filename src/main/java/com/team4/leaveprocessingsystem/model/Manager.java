package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
public class Manager extends Employee {
    @OneToMany(mappedBy = "manager")
    private List<Employee> subordinates;

    public Manager() {}
}
