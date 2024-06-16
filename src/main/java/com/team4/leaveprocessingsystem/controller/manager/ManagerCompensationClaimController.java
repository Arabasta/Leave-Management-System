package com.team4.leaveprocessingsystem.controller.manager;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Manager;
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
import java.util.Objects;

@RequestMapping("/manager/compensation-claims")
@Controller
public class ManagerCompensationClaimController {

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
    public ManagerCompensationClaimController(AuthenticationService authenticationService,
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
        MANAGER - GET - PENDING COMPENSATION CLAIMS
    */
    @GetMapping("viewPendingApproval")
    public String viewPendingApproval(Model model) {
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<CompensationClaim>> employeesPendingClaims = compensationClaimService.findPendingReviewByManager(manager);
        model.addAttribute("employeesPendingClaims", employeesPendingClaims);
        return "manager/compensation-claims/view-pending-approval";
    }

    /*
        MANAGER - GET - REVIEW COMPENSATION CLAIM
    */
    @GetMapping("approvalForm/{id}")
    public String approvalForm(@PathVariable Integer id, Model model) {
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToManagerForReview(id, manager);
        model.addAttribute("compensationClaim", claim);
        return "manager/compensation-claims/approval-form";
    }

    /*
        MANAGER - POST - REVIEW COMPENSATION CLAIM
    */
    @PostMapping("approvalForm")
    public String approvalForm(@Valid @ModelAttribute("compensationClaim") CompensationClaim claim,
                                          BindingResult result,
                                          Model model) {
        if (result.hasErrors()) {   // Return back to page if validation has errors
            model.addAttribute("compensationClaim", claim);
            return "manager/compensation-claims/approval-form";
        }
        if (claim.getClaimStatus() == CompensationClaimStatusEnum.APPROVED) {
            leaveBalanceService.updateCompensationLeave(claim);
        } else {
            claim.setClaimStatus(CompensationClaimStatusEnum.REJECTED);
        }
        claim.setComments(claim.getComments());
        claim.setReviewedDateTime(LocalDateTime.now());
        compensationClaimService.save(claim);
        return "redirect:/manager/compensation-claims/viewPendingApproval";
    }

    /*
        MANAGER - GET - VIEW ALL EMPLOYEE COMPENSATION CLAIMS
    */
    @GetMapping("viewEmployees")
    public String viewEmployees(Model model) {
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        List<CompensationClaim> claims = compensationClaimService.findByApprovingManager(manager);
        model.addAttribute("claims", claims);
        return "manager/compensation-claims/view-employee-claims";
    }

//    /*
//        WIP: MANAGER - POST - VIEW EMPLOYEE COMPENSATION CLAIMS
//    */
//    @PostMapping("viewEmployees/{id}")
//    public String viewEmployees(@PathVariable Integer id, Model model) {
//        if (id.toString().isBlank()) { return "redirect:/manager/compensation-claims/viewEmployees"; }
//        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
//        List<CompensationClaim> claims = compensationClaimService.findByApprovingManager(manager)
//                .stream().filter(c -> Objects.equals(c.getClaimingEmployee().getId(), id)).toList();
//        model.addAttribute("claims", claims);
//        return "manager/compensation-claims/view-employee-claims";
//    }
}
