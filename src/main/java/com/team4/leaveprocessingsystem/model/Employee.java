package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;


@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "EMP_TYPE")
@DiscriminatorOptions(force = true)
@SQLDelete(sql = "update employee set deleted = true where id=?") // changes deleted field into true rather than deleting data permanently
@Where(clause = "deleted=false") // read only employees where deleted=false; deprecated
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

    private boolean deleted = Boolean.FALSE;

    public Employee() {}

    public Employee(String name, JobDesignation jobDesignation,
                    Manager manager, LeaveBalance leaveBalance) {
        this.name = name;
        this.jobDesignation = jobDesignation;
        this.manager = manager;
        this.leaveBalance = leaveBalance;
    }
}
