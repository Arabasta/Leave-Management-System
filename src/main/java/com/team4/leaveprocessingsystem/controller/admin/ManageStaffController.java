package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
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
    private final CompensationClaimService compensationClaimService;
    private final LeaveApplicationService leaveApplicationService;
    private final AuthenticationService authenticationService;

    @Autowired
    public ManageStaffController(EmployeeService employeeService, JobDesignationService jobDesignationService,
                                 ManagerService managerService, LeaveBalanceService leaveBalanceService,
                                 UserService userService, CompensationClaimService compensationClaimService,
                                 LeaveApplicationService leaveApplicationService, AuthenticationService authenticationService) {
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
        this.userService = userService;
        this.compensationClaimService = compensationClaimService;
        this.leaveApplicationService = leaveApplicationService;
        this.authenticationService = authenticationService;
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

    @PostMapping("/create")
    public String createNewEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                    BindingResult bindingResult,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", new Employee());
            //model.addAttribute("autoAssignedManager", managerService.findManagerById(1));
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

    @GetMapping("/delete/{employeeId}")
    public String deleteEmployee(@PathVariable(name = "employeeId") Integer employeeId,
                                 Model model) {
        // Prevent admin from deleting own account. Prevent total loss of admin accounts.
        if(employeeId == authenticationService.getLoggedInEmployeeId()){
            throw new ServiceSaveException("Unable to delete your own account");
        }
        Employee employee = employeeService.findEmployeeById(employeeId);
        employee.setDeleted(true);
        List<User> userList = userService.findUserRolesByEmployeeId(employeeId);
        for (User user: userList){
            user.setRole(RoleEnum.ROLE_LOCKED);
        }
        employeeService.save(employee);


        // todo: to confirm if we want employeeRepository.findAll() to include "deleted" employees or not


        // todo: fix bug, soft delete works sometimes only.

        //employee.setDeleted(true);
//        employeeService.removeEmployee(employee);

        // TESTING - without soft delete, only able to remove Mikasa
        /*
        // delete user accounts, before removing employee
        List<User> employeeUserAccounts = userService.findUserRolesByEmployeeId(employeeId);

        for (User user : employeeUserAccounts) {
            userService.removeUser(user);
        }

        employeeService.removeEmployee(employee);
        */

        /*
        // check if employee is approving or claiming existing compensation claims
        List<Integer> allApprovingManagers = compensationClaimService.allApprovingManagersIds();
        List<Integer> allClaimingEmployees = compensationClaimService.allClaimingEmployees();

        // check if employee is approving or submitting application leaves
        List<Integer> allReviewingManagers = leaveApplicationService.allReviewingManagersIds();
        List<Integer> allSubmittingEmployees = leaveApplicationService.allClaimingEmployees();

        if (allApprovingManagers.contains(employeeId) || allClaimingEmployees.contains(employeeId)
        || allReviewingManagers.contains(employeeId) || allSubmittingEmployees.contains(employeeId)) {
            //employee.setDeleted(true);
            employeeService.removeEmployee(employee);
        }
        else {
            // delete user accounts, before removing employee
            List<User> employeeUserAccounts = userService.findUserRolesByEmployeeId(employeeId);

            for (User user : employeeUserAccounts) {
                //user.setDeleted(true);
                userService.removeUser(user);
            }
        }
         */

        return "redirect:/admin/manage-staff/";
    }

}