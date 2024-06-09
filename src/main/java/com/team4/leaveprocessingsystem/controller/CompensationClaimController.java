package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.ui.Model;
import com.team4.leaveprocessingsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.beans.ConstructorProperties;

@Controller
public class CompensationClaimController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompensationClaimService compensationClaimService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @ModelAttribute
    @RequestMapping("compensation-claims/view")
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
        return "compensation-claims/view";
    }
}
