package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/manage-staff-password/")
public class ManageStaffPasswordController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeService employeeService1;
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    public String search(@RequestParam(value = "query", required = false) String query,
                         @RequestParam(value = "searchType", required = false) String searchType,
                         Model model) {
        List<Employee> employees;
        if (query == null || query.isEmpty()) {
            employees = employeeService.findAllExcludeDeleted();
        } else {
            if (searchType == null || searchType.isEmpty())
                searchType = "name";

            employees = switch (searchType) {
                case "name" -> employeeService.findEmployeesByName(query);
                case "jobDesignation" -> employeeService.findEmployeesByJobDesignation(query);
                case "roleType" -> employeeService.findUsersByRoleType(query);
                default -> employeeService.findAll();
            };
        }

        model.addAttribute("employees", employees);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        model.addAttribute("viewAllExcludeDeleted", true);
        return "admin/manage-staff-password/view-all-employees";
    }

    @GetMapping("view-all-include-deleted")
    public String viewAllIncludeDeleted(@RequestParam(value = "query", required = false) String query,
                                        @RequestParam(value = "searchType", required = false) String searchType,
                                        Model model) {

        List<Employee> employees;
        if (query == null || query.isEmpty()) {
            employees = employeeService.findOnlyDeleted();
        } else {
            if (searchType == null || searchType.isEmpty())
                searchType = "name";
            employees = switch (searchType) {
                case "name" -> employeeService.findEmployeesByName(query);
                case "jobDesignation" -> employeeService.findEmployeesByJobDesignation(query);
                case "roleType" -> employeeService.findUsersByRoleType(query);
                default -> employeeService.findAll();
            };
        }

        model.addAttribute("employees", employees);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        model.addAttribute("viewAllExcludeDeleted", false);

        return "admin/manage-staff/view-all-employees";
    }

    @GetMapping("choose-account/{id}")
    public String chooseAccount(@PathVariable int id, Model model) {
            List<User> userAccount = userService.findByEmployeeId(id);
            model.addAttribute("userAccount", userAccount);
            return "admin/manage-staff-password/choose-account";
    }

    @GetMapping("change-password/{id}")
    public String changePassword(@PathVariable int id, Model model) {
        User currentAccount = userService.findById(id);
        model.addAttribute("currentAccount", currentAccount);
        return "admin/manage-staff-password/change-password-form";
    }

    @PostMapping("update-password")
    public String savePassword(@ModelAttribute("currentAccount") @Valid User
                                      user, BindingResult bindingResult,
                              Model model) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));

        if (bindingResult.hasErrors()) {
            return "admin/manage-staff-password/choose-account";
        }
        userService.save(user);
        return "redirect:/admin/manage-staff-password/";
    }

}
