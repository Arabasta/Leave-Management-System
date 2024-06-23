package com.team4.leaveprocessingsystem.service.reporting;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.dataTransferObjects.ReportingDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ReportingService {

    public ReportingDTO setForClaimsReport(ArrayList<Employee> employees, ArrayList<CompensationClaim> claims) {
        ReportingDTO reportingDTO = new ReportingDTO() {};
        reportingDTO.setEmployees(employees);
        reportingDTO.setClaims(claims);
        return reportingDTO;
    }

    public ReportingDTO setForLeavesReport(ArrayList<LeaveApplication> applications,
                                           int managerId,
                                           String keyword,
                                           String searchType,
                                           String startDate,
                                           String endDate,
                                           String leaveStatus) {
        ReportingDTO reportingDTO = new ReportingDTO();
        reportingDTO.setApplications(applications);
        reportingDTO.setManagerId(managerId);
        reportingDTO.setKeyword(keyword);
        reportingDTO.setSearchType(searchType);
        reportingDTO.setStartDate(startDate);
        reportingDTO.setEndDate(endDate);
        reportingDTO.setLeaveStatus(leaveStatus);
        return reportingDTO;
    }

}
