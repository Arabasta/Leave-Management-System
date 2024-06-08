package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.repository.CompensationClaimRepository;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import org.springframework.ui.Model;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
public class CompensationClaimController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompensationClaimService compensationClaimService;

    @ModelAttribute
    @RequestMapping("compensation-claims/view")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String viewCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUser ) {
        Optional<User> user = userRepository.findByUsername(currentUser.getUsername());
        Employee employee = user.map(User::getEmployee).orElse(null);
        model.addAttribute("employee", employee);
        model.addAttribute("compensationClaims", (employee != null ? employee.getCompensationClaims() : null));
        model.addAttribute("compensationClaimService", compensationClaimService);
        return "compensation-claims/view";
    }
}
