package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ManageStaffController(EmployeeService employeeService, JobDesignationService jobDesignationService,
                                 ManagerService managerService, LeaveBalanceService leaveBalanceService,
                                 UserService userService, CompensationClaimService compensationClaimService,
                                 LeaveApplicationService leaveApplicationService, AuthenticationService authenticationService,
                                 PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
        this.userService = userService;
        this.compensationClaimService = compensationClaimService;
        this.leaveApplicationService = leaveApplicationService;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String searchAll(@RequestParam(value = "query", required = false) String query,
                         @RequestParam(value = "searchType", required = false) String searchType,
                         Model model) {
        List<Employee> employees;
        if (query == null || query.isEmpty()) {
            employees = employeeService.findAllExcludeDeleted();
        } else {
            if (searchType == null || searchType.isEmpty()) { searchType = "name";}
            employees = switch (searchType) {
                case "name" -> employeeService.findEmployeesByName(query);
                case "jobDesignation" -> employeeService.findEmployeesByJobDesignation(query);
                case "manager" -> employeeService.findEmployeesByManager(query);
                default -> employeeService.findAll();
            };
        }

        model.addAttribute("employees", employees);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        model.addAttribute("viewAllExcludeDeleted", true);
        return "admin/manage-staff/view-all-employees";
    }

    @GetMapping("/view-all-include-deleted")
    public String viewAllIncludeDeleted(@RequestParam(value = "query", required = false) String query,
                                        @RequestParam(value = "searchType", required = false) String searchType,
                                        Model model) {

        List<Employee> employees;
        if (query == null || query.isEmpty()) {
            employees = employeeService.findOnlyDeleted(); // change this to findonlydeleted
        } else {
            if (searchType == null || searchType.isEmpty())
                searchType = "name";
            employees = switch (searchType) {
                case "name" -> employeeService.findEmployeesByName(query);
                case "jobDesignation" -> employeeService.findEmployeesByJobDesignation(query);
                case "manager" -> employeeService.findEmployeesByManager(query);
                default -> employeeService.findAll();
            };
        }

        model.addAttribute("employees", employees);
        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);
        model.addAttribute("viewAllExcludeDeleted", false);

        return "admin/manage-staff/view-all-employees";
    }

    /* ----------------------------------------- EMPLOYEES ------------------------------------------------------------*/

    @GetMapping("/edit/{employeeId}")
    public String editEmployeeDetails(@PathVariable(name = "employeeId") int employeeId,
                                      Model model) {
        // if there is a manager
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

        List<User> userListForEmployee = userService.findByEmployeeId(employeeId);

        model.addAttribute("employee", employee);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("jobDesignationList", jobDesignationList);
        model.addAttribute("managerList", managerList);
        model.addAttribute("userListForEmployee", userListForEmployee);

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
        model.addAttribute("jobDesignationList", jobDesignationList);
        model.addAttribute("isEditMode", true);
        model.addAttribute("updateSuccess", false);

        return "admin/manage-staff/create-new-employee-form";
    }

    @PostMapping("/create/employee")
    public String createNewEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                    BindingResult bindingResult,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", new Employee());
            model.addAttribute("jobDesignationList", jobDesignationService.listAllJobDesignations());
            model.addAttribute("isEditMode", true);
            model.addAttribute("updateSuccess", false);

            return "admin/manage-staff/create-new-employee-form";
        }

        LeaveBalance leaveBalance = new LeaveBalance(employee.getJobDesignation().getDefaultAnnualLeaves());
        leaveBalanceService.save(leaveBalance);

        JobDesignation jobDesignation = jobDesignationService.findByName(employee.getJobDesignation().getName());
        Employee newEmployee = new Employee(employee.getName(), jobDesignation, null, leaveBalance);

        employeeService.save(newEmployee);
        model.addAttribute("newEmployee", newEmployee);
        model.addAttribute("isEditMode", false);
        model.addAttribute("updateSuccess", true);

        return "admin/manage-staff/create-new-employee-form";
    }

    @GetMapping("/delete/{employeeId}")
    public String deleteEmployee(@PathVariable(name = "employeeId") Integer employeeId,
                                 Model model) {

        // Prevent admin from deleting own account. Prevent total loss of admin accounts.
        if(employeeId == authenticationService.getLoggedInEmployeeId()){
            throw new ServiceSaveException("Unable to delete your own account");
        }
        Employee employee = employeeService.findEmployeeById(employeeId);
        employee.setDeleted(true);
        List<User> userList = userService.findByEmployeeId(employeeId);
        for (User user: userList){
            user.setRole(RoleEnum.ROLE_LOCKED);
        }
        employeeService.save(employee);

        return "redirect:/admin/manage-staff/";
    }
    /* ----------------------------------------- USERS ------------------------------------------------------------*/

    @GetMapping("/add/user/{employeeId}")
    public String createNewUserForm(@PathVariable(name = "employeeId") Integer employeeId,
                                    Model model) {
        User user = new User();

        Employee employee = employeeService.findEmployeeById(employeeId);
        user.setEmployee(employee);

        model.addAttribute("user", user);
        model.addAttribute("roles", RoleEnum.values());

        model.addAttribute("isEditMode", true);
        model.addAttribute("updateSuccess", false);

        return "admin/manage-user/create-new-user-account-form";
    }

    @PostMapping("/create/user")
    public String createNewUser(@Valid @ModelAttribute("user") User user,
                                    BindingResult bindingResult,
                                    Model model) {

        Employee employee = employeeService.findEmployeeById(user.getEmployee().getId());

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", RoleEnum.values());

            model.addAttribute("isEditMode", true);
            model.addAttribute("updateSuccess", false);
            return "admin/manage-user/create-new-user-account-form";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        model.addAttribute("newUser", user);
        model.addAttribute("isEditMode", false);
        model.addAttribute("updateSuccess", true);

        return "admin/manage-user/create-new-user-account-form";
    }

//    @GetMapping("/edit/user/{employeeId}")
//    public String editUserDetails(@PathVariable(name = "employeeId") int employeeId,
//                                  Model model) {
//
//        Employee employee = employeeService.findEmployeeById(employeeId);
//        List<User> userListForEmployee = userService.findByEmployeeId(employeeId);
//
//        model.addAttribute("employee", employee);
//        model.addAttribute("userListForEmployee", userListForEmployee);
//        model.addAttribute("roles", RoleEnum.values());
//
//        model.addAttribute("isEditMode", true);
//        model.addAttribute("updateSuccess", false);
//
//        return "admin/manage-staff/edit-user-account-details-form";
//    }
//
//    @PostMapping("/update/user")
//    public String updateEmployeeDetails(@ModelAttribute("user") User user,
//                                        BindingResult bindingResult,
//                                        Model model) {
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            model.addAttribute("roles", RoleEnum.values());
//
//            model.addAttribute("isEditMode", true);
//            model.addAttribute("updateSuccess", false);
//            return "admin/manage-staff/edit-user-account-details-form";
//        }
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userService.save(user);
//        model.addAttribute("newUser", user);
//        model.addAttribute("isEditMode", false);
//        model.addAttribute("updateSuccess", true);
//        return "admin/manage-staff/edit-user-account-details-form";
//    }

}