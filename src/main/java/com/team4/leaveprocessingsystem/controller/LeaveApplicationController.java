package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationNotFoundException;
import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.AuthenticationService;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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

        return "redirect:/leave/personalHistory";
    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only applied/updated leave can be deleted

        return "redirect:/leave/personalHistory";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only approved leave can be cancelled

        return "redirect:/leave/personalHistory";
    }

    @GetMapping("history")
    public String subordinatesLeaveHistory(Model model){
        // todo: note; kei changed to use authService
        Employee manager = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        int managerId = manager.getId();

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

    @GetMapping("managerView")
    public String managerViewLeave(Model model) throws IllegalAccessException {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        if(!authenticationService.isLoggedInAManager()){
            throw new IllegalAccessException();
        }
        int managerId = employee.getId();
        List<LeaveApplication> subordinateLeaveApplications = leaveApplicationService.findSubordinatesLeaveApplicationsByReviewingManager_Id(managerId);
        model.addAttribute("subordinateLeaveApplications", subordinateLeaveApplications);
        return "leaveApplication/managerViewLeave";
    }

    @GetMapping("personalHistory")
    public String personalHistory(Model model){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        List<LeaveApplication> personalLeaveApplications = leaveApplicationService.findBySubmittingEmployee(employee);
        model.addAttribute("personalLeaveApplications", personalLeaveApplications);
        return "leaveApplication/personalViewLeave";
    }

    // MANAGER - GET - PENDING LEAVE APPLICATIONS
    @GetMapping("pendingLeaves")
    public String pendingLeaveApplications(Model model) {
        Manager currentManager = (Manager) employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<LeaveApplication>> pendingLeaveApplications = leaveApplicationService.findLeaveApplicationsPendingApprovalByManager(currentManager);
        model.addAttribute("pendingLeaveApplications", pendingLeaveApplications);
        return "leaveApplication/pendingLeaveApplications";
    }

    // MANAGER - GET - REVIEW LEAVE APPLICATIONS DETAILS
    @GetMapping("review/{id}")
    public String leaveApplicationsDetails(@PathVariable Integer id, Model model) {
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "leaveApplication/reviewLeave";
    }

    // MANAGER - POST - REVIEW LEAVE APPLICATION
    @PostMapping("submitLeaveApplication")
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
        return "redirect:/pendingLeaveApplications";
    }
}
