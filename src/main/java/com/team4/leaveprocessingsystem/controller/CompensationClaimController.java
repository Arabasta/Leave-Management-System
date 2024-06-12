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
@RequestMapping(value = "/compensation-claims")
public class CompensationClaimController {

    private final UserService userService;
    private final LeaveBalanceService leaveBalanceService;
    private final CompensationClaimService compensationClaimService;
    private final CompensationClaimValidator compensationClaimValidator;

    @InitBinder("compensation-claims")
    private void initCourseBinder(WebDataBinder binder) {
        binder.addValidators(compensationClaimValidator);
    }

    @Autowired
    public CompensationClaimController(UserService userService, LeaveBalanceService leaveBalanceService,
                                       CompensationClaimService compensationClaimService, CompensationClaimValidator validator) {
        this.userService = userService;
        this.leaveBalanceService = leaveBalanceService;
        this.compensationClaimService = compensationClaimService;
        this.compensationClaimValidator = validator;
    }

    // TODO: implement with Spring Security instead
//    @RequestMapping(value = "/compensation-claims/redirect-admin")
//    public String redirectNonEmployee() {
//        return "/compensation-claims/redirect-admin";
//    }

    @ModelAttribute
    @GetMapping("/history")
    public String viewCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
//        // TODO: implement redirectNonEmployee()
        Employee currentEmployee = userService.findByUsername(currentUserDetails.getUsername()).getEmployee();
        assert currentEmployee != null;
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaims", (currentEmployee.getCompensationClaims()));
        model.addAttribute("leaveBalance", leaveBalanceService.findByEmployee(currentEmployee.getId()).getCompensationLeave());
        return "compensation-claims/history";
    }

    /*
    EMPLOYEE - GET - CREATE COMPENSATION CLAIM
 */
    @ModelAttribute
    @GetMapping(value = "/create")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String createCompensationClaimPage(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        Employee currentEmployee = userService.findByUsername(currentUserDetails.getUsername()).getEmployee();
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaimService", compensationClaimService);
        model.addAttribute("compensationClaim", new CompensationClaim());
        return "compensation-claims/create";
    }

    /*
        EMPLOYEE - POST - CREATE COMPENSATION CLAIM
    */
    //TODO: validate overtimeEndDateTime isBefore LocalDateTime.now()
    @PostMapping("/create-submit")
    public String createCompensationClaim(@ModelAttribute @Valid CompensationClaim compensationClaim,
                                          @AuthenticationPrincipal UserDetails currentUserDetails,
                                          BindingResult bindingResult) {
        // Return back to page if validation has errors
        if (bindingResult.hasErrors()) {
            return "compensation-claims/create";
        }
        // Continue if no errors
        Employee currentEmployee = userService.findByUsername(currentUserDetails.getUsername()).getEmployee();
        compensationClaim.setClaimingEmployee(currentEmployee);
        compensationClaim.setApprovingManager(currentEmployee.getManager());
        float overtimeHours = (float) DateTimeCounterUtils.countCalendarHours(
                compensationClaim.getOvertimeStartDateTime(), compensationClaim.getOvertimeEndDateTime());
        compensationClaim.setOvertimeHours(overtimeHours);
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
        EMPLOYEE - GET - WITHDRAW COMPENSATION CLAIM
     */
    @GetMapping(value = "/withdraw/{id}")
    public String withdrawCompensationClaim(Model model, @PathVariable Integer id)
            throws CompensationClaimNotFoundException {
        // TODO: verify actor is logged in Employee
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.save(compensationClaim);
        // TODO: to implement / remove / refactor withdrawn/update success message
//        String message = "Compensation Claim " + compensationClaim.getId() + " was successfully withdrawn.";
//        model.addAttribute("withdrawn_message", message);
        return "redirect:/compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - UPDATE COMPENSATION CLAIM
     */
    @GetMapping("/update/{id}")
    public String updateCompensationClaimPage(@PathVariable Integer id, Model model) {
        // TODO: verify actor is logged in Employee
        // TODO: implement redirectNonEmployee()
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        model.addAttribute("compensationClaim", compensationClaim);
        return "compensation-claims/update";
    }

    /*
        EMPLOYEE - POST - UPDATE COMPENSATION CLAIM
     */
    //TODO: style new overtimeStartDateTime and overtimeEndDateTime to dd-MM-yyyy, hh-mm (verify format)
    @PostMapping("update-submit")
    public String updateCompensationClaim(@ModelAttribute @Valid CompensationClaim compensationClaim,
                                          BindingResult bindingResult) {
        // Return back to page if validation has errors
        if (bindingResult.hasErrors()) {
            return "/compensation-claims/update";
        }
        // Continue if no errors
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.UPDATED);
        float overtimeHours = compensationClaimService.overtimeHours(compensationClaim);
        compensationClaim.setOvertimeHours(overtimeHours);
        compensationClaim.setCompensationLeaveRequested(compensationClaimService.compensationLeaveRequested(overtimeHours));
        compensationClaimService.save(compensationClaim);
        return "redirect:/compensation-claims/history";
    }

    /*
        MANAGER - GET - PENDING COMPENSATION CLAIMS
    */
    @ModelAttribute
    @GetMapping("/pending")
    public String pendingCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        assert currentUser.getRole() == RoleEnum.ROLE_MANAGER; // TODO: implement redirectNonEmployee()
        Manager currentManager = (Manager) currentUser.getEmployee();
        // TODO: refactor below list into a model's Service and call it.
        // Gets list of Employees with Compensation Claims that are not APPLIED nor UPDATED
        List<Employee> employeesPendingClaimsList = currentManager
                .getSubordinates()
                .stream()
                .filter(employee -> employee
                        .getCompensationClaims()
                        .stream()
                        .anyMatch(claim -> claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.APPLIED
                        || claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.UPDATED)
                )
                .collect(Collectors.toList());
        model.addAttribute("employeesPendingClaimsList", employeesPendingClaimsList);
        model.addAttribute("compensationClaimService", compensationClaimService);
        return "compensation-claims/pending";
    }

    /*
        MANAGER - GET - REVIEW COMPENSATION CLAIM
    */
    @GetMapping("/review/{id}")
    public String reviewCompensationClaimPage(@PathVariable Integer id, Model model) {
//        // TODO: verify actor is logged in Manager
//        // TODO: implement redirectNonManager()
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        model.addAttribute("compensationClaimApproval", new CompensationClaimApproval());
        model.addAttribute("compensationClaim", compensationClaim);
        model.addAttribute("compensationClaimService", compensationClaimService);
        return "compensation-claims/review";
    }

    /*
        MANAGER - POST - REVIEW COMPENSATION CLAIM
    */
    @PostMapping("/review-submit/{id}")
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
