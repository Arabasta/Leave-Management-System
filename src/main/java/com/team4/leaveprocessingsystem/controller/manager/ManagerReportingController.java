package com.team4.leaveprocessingsystem.controller.manager;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.dataTransferObjects.DataExportDTO;
import com.team4.leaveprocessingsystem.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping("/manager/reporting")
@Controller
public class ManagerReportingController {

    private final DataExportService dataExportService;
    private final CompensationClaimService compensationClaimService;
    private final AuthenticationService authenticationService;
    private final ManagerService managerService;
    private final EmployeeService employeeService;

    public ManagerReportingController(DataExportService dataExportService,
                                      EmployeeService employeeService,
                                      AuthenticationService authenticationService,
                                      CompensationClaimService compensationClaimService,
                                      ManagerService managerService) {
        this.dataExportService = dataExportService;
        this.authenticationService = authenticationService;
        this.compensationClaimService = compensationClaimService;
        this.managerService = managerService;
        this.employeeService = employeeService;
    }

    /*
        MANAGER - GET - VIEW ALL EMPLOYEE COMPENSATION CLAIMS
    */
    @GetMapping("viewCompensationClaims")
    public String viewCompensationClaims(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "downloadCSV", defaultValue = "false", required = false) boolean downloadCSV,
                                Model model) {
        List<Employee> employees;
        if (query == null || query.isBlank()) {
            employees = employeeService.findAll();
        } else {
            employees = employeeService.findEmployeesByName(query);
        }
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        List<CompensationClaim> claims = compensationClaimService.filterByEmployeeListAndManager(employees, manager);
        model.addAttribute("claims", claims);
        model.addAttribute("exportDTO", new DataExportDTO(employees));
        if (downloadCSV) {
            model.addAttribute("claims",claims);
            return "redirect:/manager/reporting/downloadCSV";
        }
        return "manager/reporting/view-employee-claims";
    }

    /*
        MANAGER - POST - VIEW ALL EMPLOYEE COMPENSATION CLAIMS
    */
    @PostMapping("downloadCSV")
    public String downloadCSV(@ModelAttribute("exportDTO") DataExportDTO exportDTO,
                         HttpServletResponse response,
                         Model model) throws IOException {
        List<Employee> employees = exportDTO.getEmployees();
        if (employees == null || employees.isEmpty()) {
            return "redirect:/manager/reporting/viewCompensationClaims";
        }
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        List<CompensationClaim> claims = compensationClaimService.filterByEmployeeListAndManager(employees, manager);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=export.csv");
        dataExportService.downloadManagerReportingCompensationClaimsCSV(response.getWriter(), claims);
        model.addAttribute("list",claims);
        return "manager/reporting/view-employee-claims";
    }

}