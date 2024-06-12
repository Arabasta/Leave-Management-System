package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.ManagerRepository;
import com.team4.leaveprocessingsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/manage-staff")
public class ManageStaffController {

    private final EmployeeService employeeService;
    private final JobDesignationService jobDesignationService;
    private final UserService userService;
    private final ManagerRepository managerRepository;
    private final ManagerService managerService;
    @Autowired
    private LeaveBalanceService leaveBalanceService;
    private final View error;

    @Autowired
    public ManageStaffController(EmployeeService employeeService, JobDesignationService jobDesignationService, UserService userService, ManagerRepository managerRepository, ManagerService managerService, View error) {
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.userService = userService;
        this.managerRepository = managerRepository;
        this.managerService = managerService;
        this.error = error;
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
        List<JobDesignation> jobDesignationList = jobDesignationService.listAllJobDesignations();
        List<Manager> managerList = managerRepository.findAll();
        LeaveBalance leaveBalance = leaveBalanceService.findByEmployee(employeeId);
        model.addAttribute("employee", employee);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("jobDesignationList", jobDesignationList);
        model.addAttribute("managerList", managerList);

        model.addAttribute("isEditMode", true);
        // model.addAttribute("updateSuccess", false);

        return "manage-staff/edit-employee-details-form";
    }

    @PostMapping("/update")
    public String updateEmployeeDetails(
            @ModelAttribute("employee") Employee employee,
                                        Model model) {

        Manager manager = managerService.findManagerById(employee.getManager().getId());
        employee.setManager(manager);

        JobDesignation jd = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());

        employee.setJobDesignation(jd);


        LeaveBalance leaveBalance = leaveBalanceService.findLeaveBalanceById(employee.getLeaveBalance().getId());
        employee.setLeaveBalance(leaveBalance);
        employeeService.save(employee);


        model.addAttribute("isEditMode", false);
        model.addAttribute("employee", employee);
        return "manage-staff/view-all";

        //   return "redirect:/admin/manage-staff/";
    }
}