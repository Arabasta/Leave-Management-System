package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/manage-hierarchy")
public class ManageHierarchyController {
    private final ManagerService managerService;
    private final EmployeeService employeeService;
    private final JobDesignationService jobDesignationService;
    private final LeaveBalanceService leaveBalanceService;

    @Autowired
    public ManageHierarchyController(ManagerService managerService, EmployeeService employeeService,
                                     JobDesignationService jobDesignationService, LeaveBalanceService leaveBalanceService) {
        this.managerService = managerService;
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.leaveBalanceService = leaveBalanceService;
    }

    @GetMapping("/")
    public String getHierarchy(Model model) {
        Map<Integer, List<Employee>> managerSubordinatesMap = new HashMap<>();
        List<Employee> rootEmployees = new ArrayList<>();

        for (Employee employee : employeeService.findAll()) {
            // add to root if no manager
            if (employee.getManager() == null) {
                rootEmployees.add(employee);
            } else {
                int managerId = employee.getManager().getId();
                // if manager not in map, add manager to map
                if (!managerSubordinatesMap.containsKey(managerId)) {
                    managerSubordinatesMap.put(managerId, new ArrayList<>());
                }
                // add employee to manager's subordinates
                managerSubordinatesMap.get(managerId).add(employee);
            }
        }
        model.addAttribute("rootEmployees", rootEmployees);
        model.addAttribute("managerSubordinatesMap", managerSubordinatesMap);

        return "admin/manage-hierarchy/view";
    }

    @GetMapping("/edit/{employeeId}")
    public String editEmployee(@PathVariable(name = "employeeId") int employeeId, Model model) {
        Employee employee = employeeService.findEmployeeById(employeeId);
        LeaveBalance leaveBalance = leaveBalanceService.findByEmployee(employeeId);
        JobDesignation jobDesignation = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());
        List<Manager> managers = managerService.findAllManagers();

        model.addAttribute("employee", employee);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("jobDesignation", jobDesignation);
        model.addAttribute("managers", managers);

        return "admin/manage-hierarchy/edit-employee";
    }

    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute("employee") Employee employee) {
        Employee existingEmployee = employeeService.findEmployeeById(employee.getId());

        /* todo: prevent curr (if manager) from being assigned to curr.child (if manager)
             or just set curr.child.manager to curr.manager or null if curr.manager null
             and curr.manager to curr.child */
        if (employee.getManager().getId() != null) {
            Manager manager = managerService.findManagerById(employee.getManager().getId());
            existingEmployee.setManager(manager);
        } else if (existingEmployee instanceof Manager) {
            existingEmployee.setManager(null);
        }

        JobDesignation jd = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());
        existingEmployee.setJobDesignation(jd);

        LeaveBalance leaveBalance = leaveBalanceService.findLeaveBalanceById(employee.getLeaveBalance().getId());
        existingEmployee.setLeaveBalance(leaveBalance);

        employeeService.save(existingEmployee);
        return "redirect:/admin/manage-hierarchy/";
    }
}
