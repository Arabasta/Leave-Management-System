package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.dataTransferObjects.ReportingDTO;
import jakarta.persistence.Entity;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingService {

    public ReportingDTO setForClaimsReport(ArrayList<Employee> employees, ArrayList<CompensationClaim> claims) {
        ReportingDTO reportingDTO = new ReportingDTO() {};
        reportingDTO.setEmployees(employees);
        reportingDTO.setClaims(claims);
        return reportingDTO;
    }

    public ReportingDTO setForLeavesReport(ArrayList<LeaveApplication> applications) {
        ReportingDTO reportingDTO = new ReportingDTO();
        reportingDTO.setApplications(applications);
        return reportingDTO;
    }

}
