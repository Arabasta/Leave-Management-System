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

    @InitBinder
    private void initCompensationClaimBinder(WebDataBinder binder) {
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
        model.addAttribute("employee", currentEmployee.getName());
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
        assert currentEmployee != null;
        CompensationClaim compensationClaim = new CompensationClaim();
        compensationClaim.setClaimingEmployee(currentEmployee);
        compensationClaim.setApprovingManager(currentEmployee.getManager());
        model.addAttribute("compensationClaim", compensationClaim);
        return "compensation-claims/create";
    }

    /*
        EMPLOYEE - POST - CREATE COMPENSATION CLAIM
    */
    //TODO: validate overtimeEndDateTime isBefore LocalDateTime.now()
    @PostMapping("/create-submit")
    public String createCompensationClaim(@Valid @ModelAttribute("compensationClaim") CompensationClaim compensationClaim,
                                          @AuthenticationPrincipal UserDetails currentUserDetails,
                                          BindingResult result, Model model)
            throws CompensationClaimNotFoundException {
        // Return back to page if validation has errors
        if (result.hasErrors()) {
            model.addAttribute("compensationClaim", compensationClaim);
            return "compensation-claims/create";
        }
        Employee currentEmployee = userService.findByUsername(currentUserDetails.getUsername()).getEmployee();
        assert currentEmployee == compensationClaim.getClaimingEmployee();
        // Continue if no errors
        // TODO: to refactor calculation of overtimeHours using Service
        float overtimeHours = (float) DateTimeCounterUtils.countCalendarHours(
                compensationClaim.getOvertimeStartDateTime(), compensationClaim.getOvertimeEndDateTime());
        compensationClaim.setOvertimeHours(overtimeHours);
        // TODO: to refactor calculation of CompensationLeaveRequested using Service
        compensationClaim.setCompensationLeaveRequested(compensationClaimService.compensationLeaveRequested(overtimeHours));
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.APPLIED);
        compensationClaimService.save(compensationClaim);
        // END - Set CompensationClaim details - END
        return "redirect:/compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - WITHDRAW COMPENSATION CLAIM
     */
    @GetMapping(value = "/withdraw/{id}")
    public String withdrawCompensationClaim(@PathVariable Integer id,
                                            @AuthenticationPrincipal UserDetails currentUserDetails) {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        Employee currentEmployee = userService.findByUsername(currentUserDetails.getUsername()).getEmployee();
        assert currentEmployee == compensationClaim.getClaimingEmployee();
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.save(compensationClaim);
        return "redirect:/compensation-claims/history";
    }

    /*
        EMPLOYEE - GET - UPDATE COMPENSATION CLAIM
     */
    @GetMapping("/update/{id}")
    public String updateCompensationClaimPage(@PathVariable Integer id, Model model,
                                              @AuthenticationPrincipal UserDetails currentUserDetails) {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        Employee currentEmployee = userService.findByUsername(currentUserDetails.getUsername()).getEmployee();
        assert currentEmployee == compensationClaim.getClaimingEmployee();
        model.addAttribute("compensationClaim", compensationClaim);
        return "compensation-claims/update";
    }

    /*
        EMPLOYEE - POST - UPDATE COMPENSATION CLAIM
     */
    //TODO: style new overtimeStartDateTime and overtimeEndDateTime to dd-MM-yyyy, hh-mm (verify format)
    @PostMapping("update-submit")
    public String updateCompensationClaim(@Valid @ModelAttribute("compensationClaim") CompensationClaim compensationClaim,
                                          @AuthenticationPrincipal UserDetails currentUserDetails,
                                          BindingResult result, Model model) {
        // Return back to page if validation has errors
        if (result.hasErrors()) {
            model.addAttribute("compensationClaim", compensationClaim);
            return "compensation-claims/update/"+compensationClaim.getId();
        }
        Employee currentEmployee = userService.findByUsername(currentUserDetails.getUsername()).getEmployee();
        assert currentEmployee == compensationClaim.getClaimingEmployee();
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
                .filter(employee ->
                        compensationClaimService.findCompensationClaimsByEmployee(employee)
                        .stream()
                        .anyMatch(claim -> claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.APPLIED
                        || claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.UPDATED)
                )
                .collect(Collectors.toList());
        model.addAttribute("employeesPendingClaimsList", employeesPendingClaimsList);
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
        // Set Manager's review time into Employee's CompensationLeave
        compensationClaim.setReviewedDateTime(LocalDateTime.now());
        // Set Manager's comment into Employee's CompensationLeave
        compensationClaim.setComments(compensationClaim.getComments());
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

        compensationClaimService.save(compensationClaim);
        return "redirect:/compensation-claims/pending";
    }
}
