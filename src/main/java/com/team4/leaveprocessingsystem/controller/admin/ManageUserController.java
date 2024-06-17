package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/manage-user")
public class ManageUserController {
    private final EmployeeService employeeService;
    private final JobDesignationService jobDesignationService;
    private final ManagerService managerService;
    private final LeaveBalanceService leaveBalanceService;
    private final UserService userService;
    private final CompensationClaimService compensationClaimService;
    private final LeaveApplicationService leaveApplicationService;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ManageUserController(EmployeeService employeeService, JobDesignationService jobDesignationService,
                                ManagerService managerService, LeaveBalanceService leaveBalanceService,
                                UserService userService, CompensationClaimService compensationClaimService,
                                LeaveApplicationService leaveApplicationService, AuthenticationService authenticationService,
                                PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
        this.userService = userService;
        this.compensationClaimService = compensationClaimService;
        this.leaveApplicationService = leaveApplicationService;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String searchAll(@RequestParam(value = "query", required = false) String query,
                            @RequestParam(value = "searchType", required = false) String searchType,
                            Model model) {
        List<User> users;
        if (query == null || query.isEmpty()) {
            users = userService.findAll();
        } else {
            if (searchType == null || searchType.isEmpty()) {
                searchType = "name";
            }

            users = switch (searchType) {
                case "employeeid" -> {
                    try {
                        int employeeId = Integer.parseInt(query);
                        yield userService.findByEmployeeId(employeeId);
                    } catch (NumberFormatException e) {
                        yield Collections.emptyList(); // or handle the error as needed
                    }
                }
                case "email" -> List.of(userService.findByEmail(query));
                case "username" -> List.of(userService.findByUsername(query));
                case "role" -> userService.findByRole(RoleEnum.valueOf(query.toUpperCase()));
                default -> userService.findAll();
            };
        }

        model.addAttribute("users", users);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        return "admin/manage-staff/view-all-users";
    }


}
