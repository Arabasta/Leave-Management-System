package com.team4.leaveprocessingsystem.controller.manager;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.dataTransferObjects.ReportingDTO;
import com.team4.leaveprocessingsystem.service.*;
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

    public ManagerReportingController(DataExportService dataExportService,
                                      EmployeeService employeeService,
                                      AuthenticationService authenticationService,
                                      CompensationClaimService compensationClaimService,
                                      ManagerService managerService, ReportingService reportingService) {
        this.dataExportService = dataExportService;
        this.authenticationService = authenticationService;
        this.compensationClaimService = compensationClaimService;
        this.managerService = managerService;
        this.employeeService = employeeService;
        this.reportingService = reportingService;
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
                             HttpServletResponse response,
                             Model model) throws IOException {
        if (claims == null || claims.isEmpty()) {
            return "redirect:/manager/reporting/viewCompensationClaims";
        } else {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; file=export.csv");
            dataExportService.downloadManagerReportingCompensationClaimsCSV(response.getWriter(), claims);
            model.addAttribute("list",claims);
        }
        return "manager/reporting/view-employee-claims";
    }
}