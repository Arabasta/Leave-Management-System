package com.team4.leaveprocessingsystem.controller.manager;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.dataTransferObjects.ReportingDTO;
import com.team4.leaveprocessingsystem.service.auth.AuthenticationService;
import com.team4.leaveprocessingsystem.service.repo.CompensationClaimService;
import com.team4.leaveprocessingsystem.service.repo.EmployeeService;
import com.team4.leaveprocessingsystem.service.repo.LeaveApplicationService;
import com.team4.leaveprocessingsystem.service.repo.ManagerService;
import com.team4.leaveprocessingsystem.service.reporting.DataExportService;
import com.team4.leaveprocessingsystem.service.reporting.ReportingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RequestMapping("/manager/reporting")
@Controller
public class ManagerReportingController {

    private final DataExportService dataExportService;
    private final CompensationClaimService compensationClaimService;
    private final AuthenticationService authenticationService;
    private final ManagerService managerService;
    private final EmployeeService employeeService;
    private final ReportingService reportingService;
    private final LeaveApplicationService leaveApplicationService;

    public ManagerReportingController(DataExportService dataExportService,
                                      EmployeeService employeeService,
                                      AuthenticationService authenticationService,
                                      CompensationClaimService compensationClaimService,
                                      ManagerService managerService,
                                      ReportingService reportingService,
                                      LeaveApplicationService leaveApplicationService) {
        this.dataExportService = dataExportService;
        this.authenticationService = authenticationService;
        this.compensationClaimService = compensationClaimService;
        this.managerService = managerService;
        this.employeeService = employeeService;
        this.reportingService = reportingService;
        this.leaveApplicationService = leaveApplicationService;
    }

    /*
        MANAGER - GET - VIEW/SEARCH ALL SUBORDINATE(EMPLOYEE) COMPENSATION CLAIMS
    */
    @GetMapping("viewCompensationClaims")
    public String viewCompensationClaims(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "downloadEmployeeClaimsCSV", defaultValue = "false", required = false) boolean downloadCSV,
                                Model model) {
        ArrayList<Employee> employees;
        if (query == null || query.isBlank()) {
            employees = (ArrayList<Employee>) employeeService.findAll();
        } else {
            employees = (ArrayList<Employee>) employeeService.findEmployeesByName(query);
        }
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        ArrayList<CompensationClaim> claims = (ArrayList<CompensationClaim>) compensationClaimService.filterByEmployeeListAndManager(employees, manager);
        model.addAttribute("reportingDTO", reportingService.setForClaimsReport(employees, claims));
        if (downloadCSV) {
            return "redirect:/manager/reporting/downloadEmployeeClaimsCSV";
        }
        return "manager/reporting/view-employee-claims";
    }

    /*
        MANAGER - POST - DOWNLOAD EMPLOYEE COMPENSATION CLAIMS
    */
    @PostMapping("downloadEmployeeClaimsCSV")
    public String downloadEmployeeClaimsCSV(@ModelAttribute("claims") ArrayList<CompensationClaim> claims,
                             HttpServletResponse response) throws IOException {
        if (claims == null || claims.isEmpty()) {
            return "redirect:/manager/reporting/viewCompensationClaims";
        } else {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; file=export.csv");
            dataExportService.downloadManagerReportingCompensationClaimsCSV(response.getWriter(), claims);
        }
        return "manager/reporting/view-employee-claims";
    }

    /*
        MANAGER - POST - DOWNLOAD EMPLOYEE LEAVE APPLICATIONS
    */
    @PostMapping("downloadEmployeeLeaveApplicationsCSV")
    public String downloadEmployeeApplicationsCSV(@ModelAttribute("applications") ArrayList<LeaveApplication> applications,
                                                  @ModelAttribute("managerId") int managerId,
                                                  @ModelAttribute("keyword") String keyword,
                                                  @ModelAttribute("searchType") String searchType,
                                                  @ModelAttribute("startDate") String startDate,
                                                  @ModelAttribute("endDate") String endDate,
                                                  @ModelAttribute("leaveStatus") String leaveStatus,
                                            HttpServletResponse response) throws IOException {
        if (applications == null || applications.isEmpty()) {
            return "redirect:/manager/leave/managerView";
        } else {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; file=export.csv");
            ReportingDTO reportingDTO = reportingService.setForLeavesReport(applications,
                            managerId, keyword, searchType, startDate, endDate, leaveStatus);
            dataExportService.downloadManagerReportingLeaveApplicationsCSV(response.getWriter(), reportingDTO);
        }
        return "manager/leave/managerView";
    }
}