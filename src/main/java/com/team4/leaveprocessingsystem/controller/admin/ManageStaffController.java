package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "/admin/manage-staff")
public class ManageStaffController {

    private final EmployeeService employeeService;
    private final JobDesignationService jobDesignationService;
    private final UserService userService;

    @Autowired
    public ManageStaffController(EmployeeService employeeService, JobDesignationService jobDesignationService, UserService userService) {
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String search(@RequestParam(value = "keyword", required = false) String k,
                         @RequestParam(value = "searchtype", required = false) String t,
                         Model model) {

        if (k == null) model.addAttribute("staffs", employeeService.findAll());
        if (t == null) t = "";

        switch (t) {
            case (""):
                model.addAttribute("staffs", employeeService.findAll());
                break;
            case ("name"):
                model.addAttribute("staffs", employeeService.SearchEmployeeByName(k));
                break;
            case ("jobDesignation"):
                model.addAttribute("staffs", employeeService.findEmployeeByJobDesignation(k));
                break;
            case ("roleType"):
                model.addAttribute("staffs", employeeService.findUserByRoleType(k));
                break;
            default:
                return "error/404-notfound";
        }
        model.addAttribute("keyword", k);
        model.addAttribute("searchtype", t);
        return "manage-staff/view-all";
    }

    @GetMapping("/edit/{employeeId}")
    public String editEmployeeDetails(@PathVariable(name = "employeeId") int employeeId, Model model) {

        Employee employee = employeeService.findEmployeeById(employeeId);

        BiFunction<List<User>, String, Boolean> userHasThisRole = (employeeUserAccounts, userAccountRole) -> {
            return employeeUserAccounts.stream().anyMatch(userAccount -> userAccount.getRole().toString().equals(userAccountRole));
        };

        List<String> roleEnumsAsStrings = Stream.of(RoleEnum.values())
                .map(RoleEnum::name)
                .collect(Collectors.toList());

        List<User> userAccountListByEmployeeId = userService.findUserRolesByEmployeeId(employeeId);

        List<JobDesignation> jobDesignationList = jobDesignationService.listAllJobDesignations();

        model.addAttribute("employee", employee);
        model.addAttribute("userRoleflag", userHasThisRole);
        model.addAttribute("roles", roleEnumsAsStrings);
        model.addAttribute("userAccountListByEmployeeId", userAccountListByEmployeeId);
        model.addAttribute("jobDesignationList", jobDesignationList);
        model.addAttribute("isEditMode", true);

        return "manage-staff/edit-employee-details-form";
    }

    @PostMapping("/update")
    public String updateEmployeeDetails(@RequestParam("employeeId") int employeeId,
                                        @RequestParam("managerId") int managerId,
                                        @ModelAttribute Employee employee,
                                        Model model) {
        Employee existingEmployee = employeeService.findEmployeeById(employeeId);
        Employee manager = employeeService.findEmployeeById(managerId);

        if (!(manager instanceof Manager)) {
            model.addAttribute("errorMessage", "Invalid manager id");
            return "manage-staff/edit-employee-details-form";
        }

        existingEmployee.setManager((Manager) manager);
        existingEmployee.setName(employee.getName());
        JobDesignation jd = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());
        existingEmployee.setJobDesignation(jd);

        employeeService.save(existingEmployee);


        model.addAttribute("isEditMode", false);
        model.addAttribute("updateSuccess", true);
        model.addAttribute("employee", existingEmployee);

        return "redirect:/admin/manage-staff/";
    }
}