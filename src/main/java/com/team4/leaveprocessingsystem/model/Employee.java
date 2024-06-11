package com.team4.leaveprocessingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Job Designation cannot be blank")
    @OneToOne(optional = false)
    private JobDesignation jobDesignation;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.MERGE)
    private List<User> users = new ArrayList<>();

    @ManyToOne
    private Manager manager;

    @OneToOne(orphanRemoval = true)
    private LeaveBalance leaveBalance;

    @OneToMany(mappedBy = "submittingEmployee", cascade = CascadeType.MERGE)
    private List<LeaveApplication> leaveApplications = new ArrayList<>();

    @OneToMany(mappedBy = "claimingEmployee", cascade = CascadeType.MERGE)
    private List<CompensationClaim> compensationClaims = new ArrayList<>();

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

    public void setManager(Manager manager) {
        // remove employee from old manager
        if (this.manager != null && manager != this.manager) {
            this.manager.removeSubordinate(this);
        }

        this.manager = manager;
        if (manager != null) {
            manager.addSubordinate(this);
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

//    public void addUser(User user) {
//        addUser(user, true);
//    }
//    public void addUser(User user, boolean set) {
//        if (user != null) {
//            if (getUsers().contains(user)) {
//                getUsers().set(getUsers().indexOf(user), user);
//            } else {
//                getUsers().add(user);
//            }
//            if (set) {
//                user.setEmployee(this, false);
//            }
//        }
//    }
//    public void removeUser(User user) {
//        getUsers().remove(user);
//        user.setEmployee(null);
//    }
//
//    public void addLeaveApplication(LeaveApplication leaveApplication) {
//        addLeaveApplication(leaveApplication, true);
//    }
//    public void addLeaveApplication(LeaveApplication leaveApplication, boolean set) {
//        if (leaveApplication != null) {
//            if (getLeaveApplications().contains(leaveApplication)) {
//                getLeaveApplications().set(getLeaveApplications().indexOf(leaveApplication), leaveApplication);
//            } else {
//                getLeaveApplications().add(leaveApplication);
//            }
//            if (set) {
//                leaveApplication.setEmployee(this, false);
//            }
//        }
//    }
//    public void removeLeaveApplication(LeaveApplication leaveApplication) {
//        getLeaveApplications().remove(leaveApplication);
//        leaveApplication.setEmployee(null);
//    }
//
//    public void addCompensationClaim(CompensationClaim compensationClaims) {
//        addCompensationClaim(compensationClaims, true);
//    }
//    public void addCompensationClaim(CompensationClaim compensationClaims, boolean set) {
//        if (compensationClaims != null) {
//            if (getCompensationClaims().contains(compensationClaims)) {
//                getCompensationClaims().set(getCompensationClaims().indexOf(compensationClaims), compensationClaims);
//            } else {
//                getCompensationClaims().add(compensationClaims);
//            }
//            if (set) {
//                compensationClaims.setEmployee(this, false);
//            }
//        }
//    }
//    public void removeCompensationClaim(CompensationClaim compensationClaims) {
//        getCompensationClaims().remove(compensationClaims);
//        compensationClaims.setEmployee(null);
//    }
}
