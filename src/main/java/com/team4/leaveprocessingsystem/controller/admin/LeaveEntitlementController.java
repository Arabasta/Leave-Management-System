package com.team4.leaveprocessingsystem.controller.admin;


import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.service.repo.EmployeeService;
import com.team4.leaveprocessingsystem.service.repo.JobDesignationService;
import com.team4.leaveprocessingsystem.service.repo.LeaveBalanceService;
import com.team4.leaveprocessingsystem.validator.JobDesignationValidator;
import com.team4.leaveprocessingsystem.validator.LeaveBalanceValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/leave-entitlement")
public class LeaveEntitlementController {

    private final LeaveBalanceService leaveBalanceService;
    private final EmployeeService employeeService;
    private final JobDesignationService jobDesignationService;
    private final LeaveBalanceValidator leaveBalanceValidator;
    private final JobDesignationValidator jobDesignationValidator;

    @InitBinder("leave_balance")
    protected void initBinderLeaveBalance(WebDataBinder binder) {
        binder.addValidators(leaveBalanceValidator);
    }

    @InitBinder("job_designation")
    protected void initBinderJobDesignation(WebDataBinder binder) {
        binder.addValidators(jobDesignationValidator);
    }

    @Autowired
    public LeaveEntitlementController(LeaveBalanceService leaveBalanceService, EmployeeService employeeService,
                                      JobDesignationService jobDesignationService, LeaveBalanceValidator leaveBalanceValidator, JobDesignationValidator jobDesignationValidator) {
        this.leaveBalanceService = leaveBalanceService;
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.leaveBalanceValidator = leaveBalanceValidator;
        this.jobDesignationValidator = jobDesignationValidator;
    }

    @GetMapping("/view-employees")
    public String viewEmployees(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "searchType", required = false) String searchType,
                                Model model) {
        List<Employee> employees;
        if (query == null || query.isEmpty()) {
            employees = employeeService.findAll();
        } else {
            if (searchType == null || searchType.isEmpty())
                searchType = "name";

            employees = switch (searchType) {
                case "name" -> employeeService.findEmployeesByName(query);
                case "jobDesignation" -> employeeService.findEmployeesByJobDesignation(query);
                default -> employeeService.findAll();
            };
        }

        model.addAttribute("employees", employees);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        return "admin/leave-entitlement/view-employees";
    }

    @GetMapping("/view-job-designations")
    public String viewJobDesignations(@RequestParam(value = "query", required = false) String query,
                                      Model model) {
        List<JobDesignation> jobDesignations;
        if (query == null || query.isEmpty()) {
            jobDesignations = jobDesignationService.listAllJobDesignations();
        } else {
            jobDesignations = jobDesignationService.queryJobDesignationsByName(query);
        }

        model.addAttribute("jobDesignations", jobDesignations);
        model.addAttribute("query", query);
        return "admin/leave-entitlement/view-job-designations";
    }

    @GetMapping("/edit/employee/{employeeId}")
    public String editLeaveEntitlement(@PathVariable("employeeId") int employeeId, Model model) {
        LeaveBalance leaveBalance = leaveBalanceService.findByEmployee(employeeId);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("isEditMode", true);
        return "admin/leave-entitlement/edit-employee-leave";
    }

    @GetMapping("/edit/jobDesignation/{jobDesignationId}")
    public String editLeaveEntitlementByJob(@PathVariable("jobDesignationId") int jobDesignationId, Model model) {
        JobDesignation jobDesignation = jobDesignationService.findJobDesignationById(jobDesignationId);
        model.addAttribute("jobDesignation", jobDesignation);
        model.addAttribute("isEditMode", true);
        return "admin/leave-entitlement/edit-job-designation-leave";
    }

    @PostMapping("/update/employee")
    public String updateLeaveEntitlement(@RequestParam("employeeId") int employeeId,
                                         @Valid @ModelAttribute LeaveBalance leaveBalance,
                                         BindingResult bindingResult,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            return editLeaveEntitlement(employeeId, model);
        }

        leaveBalanceService.save(leaveBalance);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("isEditMode", false);
        model.addAttribute("updateSuccess", true);
        return "admin/leave-entitlement/edit-employee-leave";
    }

    @PostMapping("/update/jobDesignation")
    public String updateLeaveEntitlementByJob(@RequestParam("jobDesignationId") int jobDesignationId,
                                              @Valid @ModelAttribute JobDesignation jobDesignation,
                                              BindingResult bindingResult,
                                              Model model) {
        if (bindingResult.hasErrors()) {
            return editLeaveEntitlementByJob(jobDesignationId, model);
        }

        JobDesignation existingJobDesignation = jobDesignationService.findJobDesignationById(jobDesignationId);
        existingJobDesignation.setDefaultAnnualLeaves(jobDesignation.getDefaultAnnualLeaves());
        jobDesignationService.save(existingJobDesignation);

        model.addAttribute("jobDesignation", existingJobDesignation);
        model.addAttribute("isEditMode", false);
        model.addAttribute("updateSuccess", true);
        return "admin/leave-entitlement/edit-job-designation-leave";
    }
}
