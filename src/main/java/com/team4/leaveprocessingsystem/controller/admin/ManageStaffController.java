package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/manage-staff")
public class ManageStaffController {

    private final EmployeeService employeeService;
    private final JobDesignationService jobDesignationService;
    private final ManagerService managerService;
    private final LeaveBalanceService leaveBalanceService;
    private final UserService userService;

    @Autowired
    public ManageStaffController(EmployeeService employeeService, JobDesignationService jobDesignationService,
                                 ManagerService managerService, LeaveBalanceService leaveBalanceService, UserService userService) {
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String search(@RequestParam(value = "query", required = false) String query,
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
                case "roleType" -> employeeService.findUsersByRoleType(query);
                default -> employeeService.findAll();
            };
        }

        model.addAttribute("employees", employees);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        return "admin/manage-staff/view-all-employees";
    }

    @GetMapping("/edit/{employeeId}")
    public String editEmployeeDetails(@PathVariable(name = "employeeId") int employeeId,
                                      Model model) {
//        Employee employee;
//        if (managerService.findManagerById(employeeId) != null) {
//            List<Employee> subordinates = managerService.findManagerById(employeeId).getSubordinates();
//            List<CompensationClaim> compensationClaims = managerService.findManagerById(employeeId).getCompensationClaims();
//            List<LeaveApplication> leaveApplications = managerService.findManagerById(employeeId).getLeaveApplications();
//            model.addAttribute("subordinates", subordinates);
//            model.addAttribute("compensationClaims", compensationClaims);
//            model.addAttribute("leaveApplications", leaveApplications);
//        }
        Employee employee = employeeService.findEmployeeById(employeeId);
        LeaveBalance leaveBalance = leaveBalanceService.findByEmployee(employeeId);

        List<JobDesignation> jobDesignationList = jobDesignationService.listAllJobDesignations();
        List<Manager> managerList = managerService.findAllManagers();

        model.addAttribute("employee", employee);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("jobDesignationList", jobDesignationList);
        model.addAttribute("managerList", managerList);


        model.addAttribute("isEditMode", true);
        model.addAttribute("updateSuccess", false);

        return "admin/manage-staff/edit-employee-details-form";
    }

    @PostMapping("/update")
    public String updateEmployeeDetails(@ModelAttribute("employee") Employee employee,
                                        Model model) {
        Employee existingEmployee = employeeService.findEmployeeById(employee.getId());

        if (employee.getManager() != null && employee.getManager().getId() != null) {
            Manager manager = managerService.findManagerById(employee.getManager().getId());
            existingEmployee.setManager(manager);
        } else {
            existingEmployee.setManager(null);
        }

        existingEmployee.setName(employee.getName());

        JobDesignation jd = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());
        existingEmployee.setJobDesignation(jd);

        LeaveBalance leaveBalance = leaveBalanceService.findLeaveBalanceById(employee.getLeaveBalance().getId());
        existingEmployee.setLeaveBalance(leaveBalance);

        employeeService.save(existingEmployee);

        model.addAttribute("isEditMode", false);
        model.addAttribute("existingEmployee", existingEmployee);
        model.addAttribute("updateSuccess", true);

        return "admin/manage-staff/edit-employee-details-form";
    }


    @GetMapping("/add/employee")
    public String createNewEmployeeForm(Model model) {
        List<JobDesignation> jobDesignationList = jobDesignationService.listAllJobDesignations();
        model.addAttribute("employee", new Employee());
        model.addAttribute("autoAssignedManager", managerService.findManagerById(1));
        model.addAttribute("jobDesignationList", jobDesignationList);
        model.addAttribute("isEditMode", true);
        model.addAttribute("updateSuccess", false);

        return "admin/manage-staff/create-new-employee-form";
    }

    // todo: fix bug, cannot save employee
    @PostMapping("/create")
    public String createNewEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                    BindingResult bindingResult,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", new Employee());
            model.addAttribute("autoAssignedManager", managerService.findManagerById(1));
            //model.addAttribute("autoAssignedLeaveBalance", new LeaveBalance(14));
            model.addAttribute("jobDesignationList", jobDesignationService.listAllJobDesignations());
            model.addAttribute("isEditMode", false);
            model.addAttribute("updateSuccess", true);
            return "admin/manage-staff/create-new-employee-form";
        }

        LeaveBalance leaveBalance = new LeaveBalance(employee.getJobDesignation().getDefaultAnnualLeaves());
        leaveBalanceService.save(leaveBalance);

        JobDesignation jobDesignation = jobDesignationService.findByName(employee.getJobDesignation().getName());
        Employee newEmployee = new Employee(employee.getName(), jobDesignation, null, leaveBalance);

        employeeService.save(newEmployee);

        return "redirect:/admin/manage-staff/";
    }

    // todo: add popup to confirm if want to delete
    // todo: fix bug, see commit msg
    @GetMapping("/delete/{employeeId}")
    public String deleteEmployee(@PathVariable(name = "employeeId") Integer employeeId,
                                 Model model) {
        Employee employee = employeeService.findEmployeeById(employeeId);

        List<User> employeeUserAccounts = userService.findUserRolesByEmployeeId(employeeId);

        for (User user : employeeUserAccounts) {
            userService.removeUser(user);
        }

        employeeService.removeEmployee(employee);
        return "redirect:/admin/manage-staff/";
    }


}