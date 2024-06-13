package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/manage-hierarchy")
public class ManageHierarchyController {
    private final ManagerService managerService;
    private final EmployeeService employeeService;

    @Autowired
    public ManageHierarchyController(ManagerService managerService, EmployeeService employeeService) {
        this.managerService = managerService;
        this.employeeService = employeeService;
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
}
