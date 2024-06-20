package com.team4.leaveprocessingsystem.controller.admin;


import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.service.repo.EmployeeService;
import com.team4.leaveprocessingsystem.service.repo.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/leave-entitlement")
public class LeaveEntitlementController {

    private final LeaveBalanceService leaveBalanceService;
    private final EmployeeService employeeService;

    @Autowired
    public LeaveEntitlementController(LeaveBalanceService leaveBalanceService, EmployeeService employeeService) {
        this.leaveBalanceService = leaveBalanceService;
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String viewEmployees(Model model) {
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "admin/leave-entitlement/view-all";
    }

    @GetMapping("/edit/{employeeId}")
    public String editLeaveEntitlement(@PathVariable("employeeId") int employeeId, Model model) {
        LeaveBalance leaveBalance = leaveBalanceService.findByEmployee(employeeId);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("isEditMode", true);
        return "admin/leave-entitlement/employee-leave-form";
    }

    @PostMapping("/update")
    public String updateLeaveEntitlement(@RequestParam("employeeId") int employeeId,
                                         @ModelAttribute LeaveBalance leaveBalance,
                                         Model model) {
        leaveBalanceService.save(leaveBalance);
        //return "redirect:/admin/leave-entitlement/view-employee/" + employeeId;
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("isEditMode", false);
        model.addAttribute("updateSuccess", true);
        return "admin/leave-entitlement/employee-leave-form";
    }

}
