package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class JobDesignation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int annualLeaves;

    public JobDesignation() {}

    public JobDesignation(String name, int annualLeaves) {
        this.name = name;
        this.annualLeaves = annualLeaves;
    }

}