package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.CompensationClaimNotFoundException;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
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

@Controller
public class CompensationClaimController {

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private CompensationClaimService compensationClaimService;

    @Autowired
    private CompensationClaimValidator compensationClaimValidator;

    @InitBinder("course")
    private void initCourseBinder(WebDataBinder binder) {
        binder.addValidators(compensationClaimValidator);
    }

    @ModelAttribute
    @GetMapping("compensation-claims/history")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String viewCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        Employee currentEmployee = currentUser.getEmployee();
        model.addAttribute("isAdmin", currentUser.getRole()==RoleEnum.ROLE_ADMIN);
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaims", (currentEmployee != null ? currentEmployee.getCompensationClaims() : null));
        model.addAttribute("compensationClaimService", compensationClaimService);
        model.addAttribute("leaveBalanceService", leaveBalanceService);
        return "compensation-claims/history";
    }

    @RequestMapping(value = "compensation-claims/withdraw/{id}")
    public String deleteCompensationClaim(Model model, @PathVariable Integer id) throws CompensationClaimNotFoundException {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);

        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.changeCompensationClaim(compensationClaim);

        String message = "Compensation Claim " + compensationClaim.getId() + " was successfully withdrawn.";
        model.addAttribute("withdrawn_message",message);

        return "redirect:/compensation-claims/history";
    }

    @GetMapping("compensation-claims/update/{id}")
    public String updateCompensationClaim(@PathVariable Integer id, Model model,
                                          @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        Employee currentEmployee = currentUser.getEmployee();
        model.addAttribute("isAdmin", currentUser.getRole()==RoleEnum.ROLE_ADMIN);
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        model.addAttribute("compensationClaim", compensationClaim);

        return "compensation-claims/update";
    }

//    @PostMapping("compensation-claims/update/{id}")
//    public String editCompensationClaim(Model model, @ModelAttribute @Valid CompensationClaim compensationClaim,
//                                        BindingResult result, @PathVariable Integer id,
//                                        HttpSession session) throws CompensationClaimInvalidException {
//        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.UPDATED);
//        compensationClaimService.changeCompensationClaim(compensationClaim);
//
//        model.addAttribute("compensationClaim", compensationClaim);
//        return "compensation-claims/update";
//    }

    @ModelAttribute
    @GetMapping("compensation-claims/create")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String newCompensationClaimPage(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        Employee currentEmployee = currentUser.getEmployee();
        model.addAttribute("isAdmin", currentUser.getRole()==RoleEnum.ROLE_ADMIN);
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaimService", compensationClaimService);
        model.addAttribute("compensationClaim", new CompensationClaim());
        return "compensation-claims/create";
    }

    @PostMapping("compensation-claims/create-submit")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String createCompensationClaim(@ModelAttribute @Valid CompensationClaim compensationClaim,
                                          @AuthenticationPrincipal UserDetails currentUserDetails,
                                          BindingResult result) {
        if (result.hasErrors()) {
            return "compensation-claims/create";
        }

        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        Employee currentEmployee = currentUser.getEmployee();
        compensationClaim.setClaimingEmployee(currentEmployee);
        compensationClaim.setApprovingManager(currentEmployee.getManager());
        // TODO: ensure validation is working
        float overtimeHours = (float)DateTimeCounterUtils.countCalendarHours(
                compensationClaim.getOvertimeStartDateTime(), compensationClaim.getOvertimeEndDateTime());
        compensationClaim.setOvertimeHours(overtimeHours);
        // TODO: ensure validation is working
        int eligibleOvertimeHours = (int) overtimeHours / 4;
        compensationClaim.setCompensationLeaveRequested(eligibleOvertimeHours * 0.5f);
        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.APPLIED);
        compensationClaimService.save(compensationClaim);

        String message = "New Compensation Claim " + compensationClaim.getId() + " was successfully created.";
        System.out.println(message);
        return "redirect:/compensation-claims/history";
    }
}