package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.CompensationClaimNotFoundException;
import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.validator.CompensationClaimValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// TODO: refactor Manager / Employee methods into different controller
// TODO: refactor VIEW / CREATE / UPDATE methods into different controller
// TODO: refactor using Spring Security, maybe different views for Admin / Employee
@Controller
@RequestMapping(value = "/compensation-claims")
public class CompensationClaimController {

    private final LeaveBalanceService leaveBalanceService;
    private final CompensationClaimService compensationClaimService;
    private final AuthenticationService authenticationService;
    private final CompensationClaimValidator compensationClaimValidator;
    private final EmployeeService employeeService;

    @InitBinder
    private void initCompensationClaimBinder(WebDataBinder binder) {
        binder.addValidators(compensationClaimValidator);
    }

    @Autowired
    public CompensationClaimController(AuthenticationService authenticationService,
                                       EmployeeService employeeService,
                                       LeaveBalanceService leaveBalanceService,
                                       CompensationClaimService compensationClaimService,
                                       CompensationClaimValidator validator) {

        this.authenticationService = authenticationService;
        this.employeeService = employeeService;
        this.leaveBalanceService = leaveBalanceService;
        this.compensationClaimService = compensationClaimService;
        this.compensationClaimValidator = validator;
    }

    /*
        EMPLOYEE - GET - VIEW COMPENSATION CLAIMS
    */
    @ModelAttribute
    @GetMapping("/history")
    public String viewCompensationClaims(Model model) {
        Employee currentEmployee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        model.addAttribute("employee", currentEmployee.getName());
        model.addAttribute("compensationClaims", compensationClaimService.findCompensationClaimsByEmployee(currentEmployee));
        model.addAttribute("leaveBalance", leaveBalanceService.findByEmployee(currentEmployee.getId()).getCompensationLeave());
        return "compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - CREATE COMPENSATION CLAIM
    */
    @ModelAttribute
    @GetMapping(value = "/create")
    public String createCompensationClaimPage(Model model) {
        Employee currentEmployee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim compensationClaim = new CompensationClaim();
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.APPLIED);
        compensationClaim.setClaimingEmployee(currentEmployee);
        compensationClaim.setApprovingManager(currentEmployee.getManager());
        compensationClaim.setClaimDateTime(LocalDateTime.now());
        model.addAttribute("compensationClaim", compensationClaim);
        return "compensation-claims/create";
    }

    /*
        EMPLOYEE - POST - CREATE COMPENSATION CLAIM
    */
    @PostMapping("/create-submit")
    public String createCompensationClaim(@Valid CompensationClaim compensationClaim,
                                          BindingResult result, Model model)
            throws CompensationClaimNotFoundException {
        // Return back to page if validation has errors
        if (result.hasErrors()) {
            model.addAttribute("compensationClaim", compensationClaim);
            System.out.println(compensationClaim);
            return "compensation-claims/create";
        }
        // Continue if no errors
        compensationClaim.setOvertimeHours(compensationClaimService.calculateOvertimeHours(compensationClaim));
        compensationClaim.setCompensationLeaveRequested(compensationClaimService.calculateLeaveRequested(compensationClaim));
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.APPLIED);
        compensationClaim.setClaimDateTime(LocalDateTime.now());
        compensationClaimService.save(compensationClaim);
        // END - Set CompensationClaim details - END
        System.out.println(compensationClaim);
        return "redirect:/compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - UPDATE COMPENSATION CLAIM
    */
    @GetMapping("/update/{id}")
    public String updateCompensationClaimPage(@PathVariable Integer id, Model model) {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaimById(id);
        compensationClaim.setClaimDateTime(LocalDateTime.now());
        model.addAttribute("compensationClaim", compensationClaim);
        return "compensation-claims/update";
    }

    /*
        EMPLOYEE - POST - UPDATE COMPENSATION CLAIM
     */
    //TODO: style new overtimeStartDateTime and overtimeEndDateTime to dd-MM-yyyy, hh-mm (verify format)
    @PostMapping("/update-submit")
    public String updateCompensationClaim(@Valid CompensationClaim compensationClaim,
                                          BindingResult result, Model model) {
        // Return back to page if validation has errors
        if (result.hasErrors()) {
            // Set overtime Start and End back to original values, since they had errors
            compensationClaim.setOvertimeStartDateTime(compensationClaim.getOvertimeStartDateTime());
            compensationClaim.setOvertimeEndDateTime(compensationClaim.getOvertimeEndDateTime());
            model.addAttribute("compensationClaim", compensationClaim);
            return "compensation-claims/update";
        }
        // Continue if no errors
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.UPDATED);
        compensationClaim.setOvertimeHours(compensationClaimService.calculateOvertimeHours(compensationClaim));
        compensationClaim.setClaimDateTime(LocalDateTime.now());
        compensationClaim.setCompensationLeaveRequested(compensationClaimService.calculateLeaveRequested(compensationClaim));
        compensationClaimService.save(compensationClaim);
        System.out.println(compensationClaim);
        return "redirect:/compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - WITHDRAW COMPENSATION CLAIM
     */
    @GetMapping(value = "/withdraw/{id}")
    public String withdrawCompensationClaim(@PathVariable Integer id) {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaimById(id);
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.save(compensationClaim);
        return "redirect:/compensation-claims/history";
    }

    /*
        MANAGER - GET - PENDING COMPENSATION CLAIMS
    */
    @ModelAttribute
    @GetMapping("/pending")
    public String pendingCompensationClaims(Model model) {
        Manager currentManager = (Manager) employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<CompensationClaim>> employeesPendingClaims =
                compensationClaimService.findCompensationClaimsPendingReviewByManager(currentManager);
        model.addAttribute("employeesPendingClaims", employeesPendingClaims);
        return "compensation-claims/pending";
    }

    /*
        MANAGER - GET - REVIEW COMPENSATION CLAIM
    */
    @GetMapping("/review/{id}")
    public String reviewCompensationClaimPage(@PathVariable Integer id, Model model) {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaimById(id);
        model.addAttribute("compensationClaim", compensationClaim);
        return "compensation-claims/review";
    }

    /*
        MANAGER - POST - REVIEW COMPENSATION CLAIM
    */
    @PostMapping("/review-submit")
    public String reviewCompensationClaim(@Valid @ModelAttribute("compensationClaim") CompensationClaim compensationClaim,
                                          BindingResult result,
                                          Model model) {
        // Return back to page if validation has errors
        if (result.hasErrors()) {
            model.addAttribute("compensationClaim", compensationClaim);
            return "compensation-claims/review";
        }
        if (compensationClaim.getCompensationClaimStatus()==CompensationClaimStatusEnum.APPROVED) {
            float employeeCurrentCompensationLeaveBalance = compensationClaim
                    .getClaimingEmployee()
                    .getLeaveBalance().getCompensationLeave();
            // TODO: to refactor Update Employee's CompensationLeave
            // Update Employee's CompensationLeave
            compensationClaim.getClaimingEmployee()
                    .getLeaveBalance()
                    .setCompensationLeave(
                            employeeCurrentCompensationLeaveBalance +
                            compensationClaim.getCompensationLeaveRequested()
                    );
        } else {
            compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.REJECTED);
        }
        compensationClaim.setComments(compensationClaim.getComments());
        compensationClaim.setReviewedDateTime(LocalDateTime.now());
        compensationClaimService.save(compensationClaim);
        return "redirect:/compensation-claims/pending";
    }
}
