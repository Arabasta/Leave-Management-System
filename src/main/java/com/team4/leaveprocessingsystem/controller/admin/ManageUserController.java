package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                searchType = "username";
            }

            users = switch (searchType) {
                case "userid" -> {
                    try {
                        int userId = Integer.parseInt(query);
                        User user = userService.findById(userId);
                        yield user != null ? Collections.singletonList(user) : Collections.emptyList();
                    } catch (NumberFormatException e) {
                        yield Collections.emptyList(); // or handle the error as needed
                    }
                }
                case "email" -> {
                    User user = userService.findByEmail(query);
                    yield user != null ? Collections.singletonList(user) : Collections.emptyList();
                }
                case "username" -> {
                    User user = userService.findByUsername(query);
                    yield user != null ? Collections.singletonList(user) : Collections.emptyList();
                }
                case "role" -> userService.findByRole(RoleEnum.valueOf(query.toUpperCase()));
                default -> userService.findAll();
            };
        }

        model.addAttribute("users", users);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        return "admin/manage-user/view-all-users";
    }

    /* ----------------------------------------- USERS ------------------------------------------------------------*/
    @GetMapping("/edit/{employeeId}")
    public String editUserDetails(@PathVariable(name = "employeeId") int employeeId, Model model) {
        // Fetch the employee by ID
        Employee employee = employeeService.findEmployeeById(employeeId);

        // Fetch the user's details associated with the employee
        List<User> existingUsers = userService.findByEmployeeId(employeeId);

        // If there are multiple users, you might need to handle this differently
        User user = existingUsers.isEmpty() ? new User() : existingUsers.get(0);

        // Fetch roles (assuming you have an enum for roles)
        List<RoleEnum> roles = List.of(RoleEnum.values());

        // Add attributes to the model
        model.addAttribute("user", user);
        model.addAttribute("employee", employee);
        model.addAttribute("existingUsers", existingUsers);
        model.addAttribute("roles", roles);
        model.addAttribute("isEditMode", true);
        model.addAttribute("updateSuccess", false);

        // Return the Thymeleaf template
        return "admin/manage-user/edit-user-details-form";
    }

}
