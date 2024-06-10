package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        model.addAttribute("employee", employee);
        model.addAttribute("isEditMode", true);

        return "manage-staff/edit-employee-details-form";
    }

    @PostMapping("/update")
    public String updateEmployeeDetails(@RequestParam("employeeId") int employeeId,
                                        @ModelAttribute Employee employee,
                                        Model model) {
        Employee existingEmployee = employeeService.findEmployeeById(employeeId);

        if (employee.getJobDesignation() == null) {
            employee.setJobDesignation(existingEmployee.getJobDesignation());
        }
        employeeService.save(employee);

        model.addAttribute("isEditMode", false);
        model.addAttribute("updateSuccess", true);

        return "redirect:/admin/manage-staff/";
    }
}