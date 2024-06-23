package com.team4.leaveprocessingsystem.model.dataTransferObjects;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ReportingDTO {
    private ArrayList<Employee> employees;
    private ArrayList<CompensationClaim> claims;

    // Search Parameters for Manager View Leave Applications
    private ArrayList<LeaveApplication> applications;
    private int managerId;
    private String keyword;
    private String searchType;
    private String startDate;
    private String endDate;
    private String leaveStatus;

    public ReportingDTO() {};
}
