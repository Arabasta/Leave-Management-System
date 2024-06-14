package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.validator.CompensationClaimValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// TODO: refactor Manager / Employee methods into different controller
@Controller
@RequestMapping(value = "/compensation-claims")
public class CompensationClaimController {

    private final LeaveBalanceService leaveBalanceService;
    private final CompensationClaimService compensationClaimService;
    private final AuthenticationService authenticationService;
    private final CompensationClaimValidator compensationClaimValidator;
    private final EmployeeService employeeService;
    private final ManagerService managerService;

    @InitBinder
    private void initCompensationClaimBinder(WebDataBinder binder) {
        binder.addValidators(compensationClaimValidator);
    }

    @Autowired
    public CompensationClaimController(AuthenticationService authenticationService,
                                       EmployeeService employeeService,
                                       ManagerService managerService,
                                       LeaveBalanceService leaveBalanceService,
                                       CompensationClaimService compensationClaimService,
                                       CompensationClaimValidator validator) {

        this.authenticationService = authenticationService;
        this.employeeService = employeeService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
        this.compensationClaimService = compensationClaimService;
        this.compensationClaimValidator = validator;
    }

    /*
        EMPLOYEE - GET - VIEW COMPENSATION CLAIMS
    */
    @GetMapping("/view-all")
    public String view(Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        model.addAttribute("employee", employee.getName());
        model.addAttribute("leaveBalance", leaveBalanceService.findByEmployee(employee.getId()).getCompensationLeave());
        model.addAttribute("compensationClaims", compensationClaimService.findByEmployee(employee));
        return "compensation-claims/view-all";
    }

    /*
    EMPLOYEE - GET - VIEW COMPENSATION CLAIM DETAILS
*/
    @GetMapping("/view/{id}")
    public String viewDetails(@PathVariable Integer id, Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToEmployee(id, employee);
        model.addAttribute("compensationClaim", claim);
        return "compensation-claims/view-details";
    }

    /*
        EMPLOYEE - GET - CREATE COMPENSATION CLAIM
    */
    @GetMapping(value = "/create-form")
    public String viewCreateForm(Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.getNewClaimForEmployee(employee);
        model.addAttribute("compensationClaim", claim);
        return "compensation-claims/create-form";
    }

    /*
        EMPLOYEE - POST - CREATE COMPENSATION CLAIM
    */
    @PostMapping("/create-submit")
    public String createForm(@Valid CompensationClaim claim,BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("compensationClaim", claim);
            return "compensation-claims/create-form";
        } // Else
        compensationClaimService.setNewClaimAndSave(claim);
        return "redirect:/compensation-claims/view";
    }

    /*
        EMPLOYEE - GET - UPDATE COMPENSATION CLAIM
    */
    @GetMapping("/update/{id}")
    public String viewUpdateForm(@PathVariable Integer id, Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToEmployee(id, employee);
        claim.setClaimDateTime(LocalDateTime.now());
        model.addAttribute("compensationClaim", claim);
        return "compensation-claims/update-form";
    }

    /*
        EMPLOYEE - POST - UPDATE COMPENSATION CLAIM
     */
    //TODO: style new overtimeStartDateTime and overtimeEnd to dd-MM-yyyy, hh-mm (verify format)
    @PostMapping("/update-submit")
    public String updateForm(@Valid CompensationClaim claim, BindingResult result, Model model) {
        if (result.hasErrors()) { // Set back to original values, since they had errors
            claim.setOvertimeStart(claim.getOvertimeStart());
            claim.setOvertimeEnd(claim.getOvertimeEnd());
            model.addAttribute("compensationClaim", claim);
            return "compensation-claims/update-form";
        }
        compensationClaimService.setUpdateClaimAndSave(claim);
        return "redirect:/compensation-claims/view";
    }

    /*
        EMPLOYEE - GET - WITHDRAW COMPENSATION CLAIM
     */
    @GetMapping(value = "/withdraw/{id}")
    public String withdraw(@PathVariable Integer id) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToEmployee(id, employee);
        claim.setClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.save(claim);
        return "redirect:/compensation-claims/view";
    }

    /*
        MANAGER - GET - PENDING COMPENSATION CLAIMS
    */
    @GetMapping("/view-pending-approval")
    public String viewPendingApproval(Model model) {
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<CompensationClaim>> employeesPendingClaims = compensationClaimService.findPendingReviewByManager(manager);
        model.addAttribute("employeesPendingClaims", employeesPendingClaims);
        return "compensation-claims/view-pending-approval";
    }

    /*
        MANAGER - GET - REVIEW COMPENSATION CLAIM
    */
    @GetMapping("approval/{id}")
    public String viewApprovalForm(@PathVariable Integer id, Model model) {
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToManagerForReview(id, manager);
        model.addAttribute("compensationClaim", claim);
        return "compensation-claims/approval-form";
    }

    /*
        MANAGER - POST - REVIEW COMPENSATION CLAIM
    */
    @PostMapping("/approval-submit")
    public String approvalForm(@Valid @ModelAttribute("compensationClaim") CompensationClaim claim,
                                          BindingResult result,
                                          Model model) {
        if (result.hasErrors()) {   // Return back to page if validation has errors
            model.addAttribute("compensationClaim", claim);
            return "compensation-claims/approval-form";
        }
        if (claim.getClaimStatus() == CompensationClaimStatusEnum.APPROVED) {
            leaveBalanceService.updateCompensationLeave(claim);
        } else {
            claim.setClaimStatus(CompensationClaimStatusEnum.REJECTED);
        }
        claim.setComments(claim.getComments());
        claim.setReviewedDateTime(LocalDateTime.now());
        compensationClaimService.save(claim);
        return "redirect:/compensation-claims/view-pending-approval";
    }
}
