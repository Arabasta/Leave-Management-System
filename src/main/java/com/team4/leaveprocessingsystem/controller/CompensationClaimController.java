package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.CompensationClaimNotFoundException;
import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.UserService;
import com.team4.leaveprocessingsystem.util.DateTimeCounterUtils;
import com.team4.leaveprocessingsystem.validator.CompensationClaimValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// TODO: refactor Manager / Employee methods into different controller
// TODO: refactor VIEW / CREATE / UPDATE methods into different controller
// TODO: refactor using Spring Security, maybe different views for Admin / Employee
// TODO: refactor detection of logged in User to use Sessions
@Controller
public class CompensationClaimController {

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private CompensationClaimService compensationClaimService;

    // TODO: review usage of CompensationClaimValidator
    @Autowired
    private CompensationClaimValidator compensationClaimValidator;

    @InitBinder("course")
    private void initCourseBinder(WebDataBinder binder) {
        binder.addValidators(compensationClaimValidator);
    }

    /*
        EMPLOYEE - GET - VIEW COMPENSATION CLAIMS
     */
    @ModelAttribute
    @GetMapping("/compensation-claims/history")
    public String viewCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        // TODO: implement redirectNonEmployee()
        Employee currentEmployee = currentUser.getEmployee();
        assert currentEmployee != null;
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaims", (currentEmployee.getCompensationClaims()));
        model.addAttribute("compensationClaimService", compensationClaimService);
        model.addAttribute("leaveBalance", leaveBalanceService.findByEmployee(currentEmployee.getId()).getCompensationLeave());
        return "/compensation-claims/history";
    }

    /*
        EMPLOYEE - POST - WITHDRAW COMPENSATION CLAIM
     */
    @PostMapping(value = "/compensation-claims/withdraw/{id}")
    public String deleteCompensationClaim(Model model, @PathVariable Integer id) throws CompensationClaimNotFoundException {
        // TODO: verify actor is logged in Employee
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.changeCompensationClaim(compensationClaim);
        // TODO: to implement / remove / refactor withdrawn/update success message
//        String message = "Compensation Claim " + compensationClaim.getId() + " was successfully withdrawn.";
//        model.addAttribute("withdrawn_message", message);
        return "redirect:/compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - UPDATE COMPENSATION CLAIM
     */
    @GetMapping("/compensation-claims/update/{id}")
    public String updateCompensationClaimPage(@PathVariable Integer id, Model model) {
        // TODO: verify actor is logged in Employee
        // TODO: implement redirectNonEmployee()
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        model.addAttribute("compensationClaim", compensationClaim);
        return "/compensation-claims/update";
    }

    /*
        EMPLOYEE - POST - UPDATE COMPENSATION CLAIM
     */
    //TODO: style new overtimeStartDateTime and overtimeEndDateTime to dd-MM-yyyy, hh-mm (verify format)
    @PostMapping("compensation-claims/update-submit")
    public String updateCompensationClaim(@ModelAttribute @Valid CompensationClaim compensationClaim) {
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.UPDATED);
        float overtimeHours = compensationClaimService.overtimeHours(compensationClaim);
        compensationClaim.setOvertimeHours(overtimeHours);
        compensationClaim.setCompensationLeaveRequested(compensationClaimService.compensationLeaveRequested(overtimeHours));
        compensationClaimService.save(compensationClaim);
        return "redirect:/compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - CREATE COMPENSATION CLAIM
     */
    @ModelAttribute
    @GetMapping(value = "/compensation-claims/create")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String createCompensationClaimPage(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        // TODO: implement redirectNonEmployee()
        Employee currentEmployee = currentUser.getEmployee();
        assert currentEmployee != null;
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaimService", compensationClaimService);
        model.addAttribute("compensationClaim", new CompensationClaim());
        return "/compensation-claims/create";
    }

    /*
        EMPLOYEE - POST - CREATE COMPENSATION CLAIM
    */
    //TODO: validate overtimeEndDateTime isBefore LocalDateTime.now()
    @PostMapping("/compensation-claims/create-submit")
    public String createCompensationClaim(@ModelAttribute @Valid CompensationClaim compensationClaim,
                                          @AuthenticationPrincipal UserDetails currentUserDetails,
                                          BindingResult result) {
        if (result.hasErrors()) {
            return "/compensation-claims/create";
        }
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        // TODO: verify actor is logged in Employee
        // TODO: implement redirectNonEmployee()
        // START - Set CompensationClaim details - START
        Employee currentEmployee = currentUser.getEmployee();
        assert currentEmployee != null;
        compensationClaim.setClaimingEmployee(currentEmployee);
        compensationClaim.setApprovingManager(currentEmployee.getManager());
        // TODO: to implement validation is working
        float overtimeHours = (float) DateTimeCounterUtils.countCalendarHours(
                compensationClaim.getOvertimeStartDateTime(), compensationClaim.getOvertimeEndDateTime());
        compensationClaim.setOvertimeHours(overtimeHours);
        // TODO: to implement validation is working
        // TODO: to refactor calculation of eligibleOvertimeHours using Service
        compensationClaim.setCompensationLeaveRequested(compensationClaimService.compensationLeaveRequested(overtimeHours));
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.APPLIED);
        compensationClaimService.save(compensationClaim);
        // END - Set CompensationClaim details - END

        String message = "New Compensation Claim " + compensationClaim.getId() + " was successfully created.";
        System.out.println(message);
        return "redirect:/compensation-claims/history";
    }

    /*
        MANAGER - GET - PENDING COMPENSATION CLAIMS
    */
    @ModelAttribute
    @GetMapping("/compensation-claims/pending")
    public String pendingCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        assert currentUser.getRole() == RoleEnum.ROLE_MANAGER; // TODO: implement redirectNonEmployee()
        Manager currentManager = (Manager) currentUser.getEmployee();
        // TODO: refactor below list into a model's Service and call it.
        // Gets list of Employees with Compensation Claims that are not APPLIED nor UPDATED
        List<Employee> employeesWithClaimsList = currentManager
                .getSubordinates()
                .stream()
                .filter(employee -> employee
                        .getCompensationClaims()
                        .stream()
                        .anyMatch(claim -> claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.APPLIED
                        || claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.UPDATED)
                )
                .collect(Collectors.toList());
        model.addAttribute("employeesWithClaimsList", employeesWithClaimsList);
        model.addAttribute("compensationClaimService", compensationClaimService);
        return "/compensation-claims/pending";
    }

    /*
        MANAGER - GET - REVIEW COMPENSATION CLAIM
    */
    @GetMapping("/compensation-claims/review/{id}")
    public String reviewCompensationClaimPage(@PathVariable Integer id, Model model) {
//        // TODO: verify actor is logged in Manager
//        // TODO: implement redirectNonManager()
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        model.addAttribute("compensationClaimApproval", new CompensationClaimApproval());
        model.addAttribute("compensationClaim", compensationClaim);
        model.addAttribute("compensationClaimService", compensationClaimService);
        return "/compensation-claims/review";
    }

    /*
        MANAGER - POST - REVIEW COMPENSATION CLAIM
    */
    @PostMapping("compensation-claims/review-submit/{id}")
    public String reviewCompensationClaim(@ModelAttribute("compensationClaimApproval") @Valid CompensationClaimApproval approval,
                                          @PathVariable Integer id) {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        // Set Manager's review time into Employee's CompensationLeave
        compensationClaim.setReviewedDateTime(LocalDateTime.now());
        // Set Manager's comment into Employee's CompensationLeave
        compensationClaim.setComments(approval.getComment());

        if (approval.getDecision().trim().equalsIgnoreCase(CompensationClaimStatusEnum.APPROVED.toString())) {
            compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.APPROVED);
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

        compensationClaimService.save(compensationClaim);
        return "redirect:/compensation-claims/pending";
    }
}
