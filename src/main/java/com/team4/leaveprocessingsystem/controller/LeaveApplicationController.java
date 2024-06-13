package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationNotFoundException;
import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.AuthenticationService;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// todo: add and refactor into a LeaveHistoryController
@RequestMapping("leave")
@Controller
public class LeaveApplicationController {
    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;
    private final LeaveApplicationValidator leaveApplicationValidator;
    private final AuthenticationService authenticationService;

    @InitBinder
    private void initLeaveApplicationBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @Autowired
    public LeaveApplicationController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService,
                                       AuthenticationService authenticationService, LeaveApplicationValidator leaveApplicationValidator) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.authenticationService = authenticationService;
        this.leaveApplicationValidator = leaveApplicationValidator;
    }

    @GetMapping("create")
    public String createLeave(Model model){
        LeaveApplication leaveApplication = new LeaveApplication();
        // todo: note; kei changed to use authService

        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(employee.getManager());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);

        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());

        return "leaveApplication/leaveForm";
    }

    @GetMapping("edit/{id}")
    public String editLeave(@PathVariable int id, Model model){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee);

        // Only allow editing of leaves pending approval
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED){
            throw new LeaveApplicationNotFoundException("Leave application cannot be updated");
        }

        leaveApplication.setLeaveStatus(LeaveStatusEnum.UPDATED);
        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());

        return "leaveApplication/leaveForm";
    }

    @PostMapping("save")
    public String saveLeave(@Valid @ModelAttribute("leave") LeaveApplication leaveApplication, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("leave", leaveApplication);
            model.addAttribute("leaveTypes", LeaveTypeEnum.values());
            return "leaveApplication/leaveForm";
        }

        leaveApplicationService.save(leaveApplication);

        return "redirect:/leave/history";
    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only applied/updated leave can be deleted

        return "redirect:/leave/history";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only approved leave can be cancelled

        return "redirect:/leave/history";
    }

    @GetMapping("history")
    public String subordinatesLeaveHistory(Model model){
        // todo: note; kei changed to use authService
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        int managerId = employee.getManager().getId();
        List<LeaveApplication> allLeavesbyManagerSubordinates = leaveApplicationService.findSubordinatesLeaveApplicationsByReviewingManager_Id(managerId);
        model.addAttribute("leaveApplications",allLeavesbyManagerSubordinates);

        return "leaveApplication/viewLeaveHistory";
    }


    @GetMapping("view/{id}")
    public String viewLeave(Model model, @PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee);
        model.addAttribute("leave", leaveApplication);
        return "leaveApplication/viewLeave";
    }

    private LeaveApplication getLeaveApplicationIfBelongsToEmployee(int id){
        // Get the user object that is logged in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Employee employee = user.getEmployee();

        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        // Ensure an employee only accesses his own leave applications
            if (!leaveApplication.getSubmittingEmployee().getId().equals(employee.getId())){
                throw new LeaveApplicationNotFoundException("Leave Application Not Found");
            }
        return leaveApplication;
    }

    //manager can't view his subordinates leave applications history if i use "getLeaveApplicationIfBelongsToEmployee()"
    //so i create a new method
    @GetMapping("managerView/{id}")
    public String managerViewLeave(Model model, @PathVariable int id){
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "leaveApplication/viewLeave";
    }

    @GetMapping("personalHistory")
    public String personalHistory(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Employee employee = user.getEmployee();
        int employeeId = employee.getId();

        List<LeaveApplication> personalLeaveApplications = leaveApplicationService.findLeaveApplicationsById(employeeId);
        model.addAttribute("personalLeaveApplications", personalLeaveApplications);
        return "leaveApplication/personalViewLeave";
    }

    // MANAGER - GET - PENDING LEAVE APPLICATIONS
    @GetMapping("/pendingleaveapplications")
    public String pendingleaveapplications(Model model) {
        Manager currentManager = (Manager) employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<LeaveApplication>> pendingLeaveApplications = leaveApplicationService.findLeaveApplicationsPendingApprovalByManager(currentManager);
        model.addAttribute("pendingLeaveApplications", pendingLeaveApplications);
        return "leaveApplication/pendingleaveapplications";
    }

    // MANAGER - GET - REVIEW LEAVE APPLICATIONS DETAILS
    @GetMapping("/review/{id}")
    public String leaveapplicationsDetails(@PathVariable Integer id, Model model) {
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "leaveApplication/reviewLeave";
    }

    // MANAGER - POST - REVIEW LEAVE APPLICATION
    @PostMapping("/submitLeaveApplication")
    public String reviewLeaveApplication(@Valid @ModelAttribute("leave") LeaveApplication leave, BindingResult bindingResult, Model model) {
        // Return back to page if validation has errors
        if (bindingResult.hasErrors()) {
            return "leaveApplication/reviewLeave";
        }

        // Check if the leave is rejected and ensure the rejection reason is provided
        if (leave.getLeaveStatus() == LeaveStatusEnum.REJECTED && (leave.getRejectionReason() == null || leave.getRejectionReason().trim().isEmpty())) {
            bindingResult.rejectValue("rejectionReason", "error.leave", "Rejection reason must be provided if the leave is rejected");
            return "leaveApplication/reviewLeave";
        }

        // Save the leave application status
        leaveApplicationService.save(leave);
        return "redirect:/pendingleaveapplications";
    }


}
