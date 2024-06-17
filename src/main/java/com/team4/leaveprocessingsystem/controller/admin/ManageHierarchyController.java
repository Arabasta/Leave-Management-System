package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.ManagerService;
import org.hibernate.service.spi.ServiceException;
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

    @GetMapping("/list")
    public String viewList(@RequestParam(value = "query", required = false) String query,
                           @RequestParam(value = "searchType", required = false) String searchType,
                           Model model) {
        List<Employee> employees;
        if (query == null || query.isEmpty()) {
            employees = employeeService.findAll();
        } else {
            if (searchType == null || searchType.isEmpty())
                searchType = "name";

            employees = switch (searchType) {
                case "name" -> employeeService.findEmployeesByName(query);
                case "jobDesignation" -> employeeService.findEmployeesByJobDesignation(query);
                case "manager" -> managerService.findManagersByName(query);
                default -> employeeService.findAll();
            };
        }

        model.addAttribute("employees", employees);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        return "admin/manage-hierarchy/view-list";
    }

    @GetMapping("/tree")
    public String viewTree(Model model) {
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

        return "admin/manage-hierarchy/view-tree";
    }

    @GetMapping("/edit/{employeeId}")
    public String editEmployee(@PathVariable(name = "employeeId") int employeeId, Model model) {
        Employee employee = employeeService.findEmployeeById(employeeId);
        LeaveBalance leaveBalance = leaveBalanceService.findByEmployee(employeeId);
        JobDesignation jobDesignation = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());
        List<Manager> managers = managerService.findAllManagers();

        model.addAttribute("employee", employee);
        model.addAttribute("oldManager", employee.getManager());
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("jobDesignation", jobDesignation);
        model.addAttribute("managers", managers);

        return "admin/manage-hierarchy/edit-employee";
    }

    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute("employee") Employee employee,
                                 @RequestParam("oldManager") Integer oldManagerId) {
        Employee currEmployee = employeeService.findEmployeeById(employee.getId());

        try {
            // if curr split to new tree
            if (employee.getManager().getId() == null) {
                currEmployee.setManager(null);
            } // if curr normal employee (no subordinates)
            else if (!(currEmployee instanceof Manager)) {
                currEmployee.setManager(managerService.findManagerById(employee.getManager().getId()));
            } // else curr is manager and not set to new tree
            // check and handle if curr reassigned to subordinates
            else {
                // check if new is in curr.subtree
                List<Employee> currChilds = employeeService.findEmployeesByManager((Manager) currEmployee);
                List<Employee> currMChilds = new ArrayList<>();
                for (Employee e : currChilds) {
                    // add all curr.prev to list only for Manager curr.prevs
                    if (e.getManager() == currEmployee && e instanceof Manager) {
                        currMChilds.add(e);
                    }
                }

                // if curr.child has managers
                if (!currMChilds.isEmpty()) {
                    // set curr.MChilds to curr.next
                    Manager currNext = oldManagerId != -1 ? managerService.findManagerById(oldManagerId) : null;
                    for (Employee currPrev : currMChilds) {
                        currPrev.setManager(currNext);
                        employeeService.save(currPrev);
                    }
                }
                Manager newManager = managerService.findManagerById(employee.getManager().getId());
                System.out.println(newManager.getName());
                // set curr.next = new
                currEmployee.setManager(newManager);
            }
        } catch (Exception e) {
            throw new ServiceException("Failed to update hierarchy", e);
        }

        JobDesignation jd = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());
        currEmployee.setJobDesignation(jd);

        LeaveBalance leaveBalance = leaveBalanceService.findLeaveBalanceById(employee.getLeaveBalance().getId());
        currEmployee.setLeaveBalance(leaveBalance);

        employeeService.save(currEmployee);
        return "redirect:/admin/manage-hierarchy/tree";
    }
}
