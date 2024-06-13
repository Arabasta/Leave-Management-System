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

import java.util.List;

@Controller
@RequestMapping(value = "/admin/manage-staff")
public class ManageStaffController {

    private final EmployeeService employeeService;
    private final JobDesignationService jobDesignationService;
    private final ManagerService managerService;
    private final LeaveBalanceService leaveBalanceService;

    @Autowired
    public ManageStaffController(EmployeeService employeeService, JobDesignationService jobDesignationService,
                                 ManagerService managerService, LeaveBalanceService leaveBalanceService) {
        this.employeeService = employeeService;
        this.jobDesignationService = jobDesignationService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
    }

    @GetMapping("/")
    public String search(@RequestParam(value = "query", required = false) String query,
                         @RequestParam(value = "searchType", required = false) String searchType,
                         Model model) {

        if (query == null) model.addAttribute("employees", employeeService.findAll());
        if (searchType == null) searchType = "";

        switch (searchType) {
            case (""):
                model.addAttribute("employees", employeeService.findAll());
                break;
            case ("name"):
                model.addAttribute("employees", employeeService.findEmployeesByName(query));
                break;
            case ("jobDesignation"):
                model.addAttribute("employees", employeeService.findEmployeesByJobDesignation(query));
                break;
            case ("roleType"):
                model.addAttribute("employees", employeeService.findUsersByRoleType(query));
                break;
            default:
                return "error/404-notfound";
        }
        model.addAttribute("keyword", query);
        model.addAttribute("searchtype", searchType);
        return "admin/manage-staff/view-all";
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
        JobDesignation jd = jobDesignationService.findJobDesignationById(employee.getJobDesignation().getId());
        existingEmployee.setJobDesignation(jd);

        LeaveBalance leaveBalance = leaveBalanceService.findLeaveBalanceById(employee.getLeaveBalance().getId());
        existingEmployee.setLeaveBalance(leaveBalance);

//        if (employee instanceof Manager) {
//            ((Manager) employee).setSubordinates(((Manager) employee).getSubordinates());
//            ((Manager) employee).setLeaveApplications(((Manager) employee).getLeaveApplications());
//            ((Manager) employee).setCompensationClaims(((Manager) employee).getCompensationClaims());
//            employeeService.save(employee);
//        }
        //else

        employeeService.save(existingEmployee);

        model.addAttribute("isEditMode", false);
        model.addAttribute("employee", employee);
        return "admin/manage-staff/edit-employee-details-form";
    }
}