package com.team4.leaveprocessingsystem.controller.employee;

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

@RequestMapping("/employee/compensation-claims")
@Controller
public class EmployeeCompensationClaimController {

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
    public EmployeeCompensationClaimController(AuthenticationService authenticationService,
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
    @GetMapping("view")
    public String view(Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        model.addAttribute("employee", employee.getName());
        model.addAttribute("leaveBalance", leaveBalanceService.findByEmployee(employee.getId()).getCompensationLeave());
        model.addAttribute("compensationClaims", compensationClaimService.findByEmployee(employee));
        return "employee/compensation-claims/view-all";
    }

    /*
    EMPLOYEE - GET - VIEW COMPENSATION CLAIM DETAILS
*/
    @GetMapping("viewDetails/{id}")
    public String viewDetails(@PathVariable Integer id, Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToEmployee(id, employee);
        model.addAttribute("compensationClaim", claim);
        return "employee/compensation-claims/view-details";
    }

    /*
        EMPLOYEE - GET - CREATE COMPENSATION CLAIM
    */
    @GetMapping("create")
    public String create(Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.getNewClaimForEmployee(employee);
        model.addAttribute("compensationClaim", claim);
        return "employee/compensation-claims/create-form";
    }

    /*
        EMPLOYEE - POST - CREATE COMPENSATION CLAIM
    */
    @PostMapping("create")
    public String create(@Valid CompensationClaim claim,BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("compensationClaim", claim);
            return "employee/compensation-claims/create-form";
        } // Else
        compensationClaimService.setNewClaimAndSave(claim);
        return "redirect:/employee/compensation-claims/view";
    }

    /*
        EMPLOYEE - GET - UPDATE COMPENSATION CLAIM
    */
    @GetMapping("updateForm/{id}")
    public String updateForm(@PathVariable Integer id, Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToEmployee(id, employee);
        claim.setClaimDateTime(LocalDateTime.now());
        model.addAttribute("compensationClaim", claim);
        return "employee/compensation-claims/update-form";
    }

    /*
        EMPLOYEE - POST - UPDATE COMPENSATION CLAIM
     */
    @PostMapping("updateForm")
    public String updateForm(@Valid CompensationClaim claim, BindingResult result, Model model) {
        if (result.hasErrors()) { // Set back to original values, since they had errors
            claim.setOvertimeStart(claim.getOvertimeStart());
            claim.setOvertimeEnd(claim.getOvertimeEnd());
            model.addAttribute("compensationClaim", claim);
            return "employee/compensation-claims/update-form";
        }
        compensationClaimService.setUpdateClaimAndSave(claim);
        return "redirect:/employee/compensation-claims/view";
    }

    /*
        EMPLOYEE - GET - WITHDRAW COMPENSATION CLAIM
     */
    @GetMapping(value = "withdraw/{id}")
    public String withdraw(@PathVariable Integer id) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToEmployee(id, employee);
        claim.setClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.save(claim);
        return "redirect:/employee/compensation-claims/view";
    }
}
