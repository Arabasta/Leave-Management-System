package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationUpdateException;
import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.util.EmailBuilderUtils;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    private final EmailApiService emailApiService;
    private final UserService userService;

    @InitBinder
    private void initLeaveApplicationBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @Autowired
    public LeaveApplicationController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService,
                                      AuthenticationService authenticationService, LeaveApplicationValidator leaveApplicationValidator,
                                      EmailApiService emailApiService, UserService userService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.authenticationService = authenticationService;
        this.leaveApplicationValidator = leaveApplicationValidator;
        this.emailApiService = emailApiService;
        this.userService = userService;
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
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());

        // Only allow editing of leaves pending approval
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED){
            throw new LeaveApplicationUpdateException("Leave application cannot be edited");
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

        // Send email notification to the manager
        try {
            String emailAdd = userService.findUserRolesByEmployeeId(leaveApplication.getReviewingManager().getId()).get(0).getEmail();
            Map<String, String> email =  EmailBuilderUtils.buildNotificationEmail(leaveApplication);
            emailApiService.sendEmail(emailAdd, email.get("subject"), email.get("text"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/leave/personalHistory";

    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        // Only applied/updated leave can be deleted
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED){
            throw new LeaveApplicationUpdateException("Leave application cannot be deleted");
        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        return "redirect:/leave/personalHistory";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        // Only approved leave can be cancelled
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPROVED){
            throw new LeaveApplicationUpdateException("Leave application cannot be cancelled");
        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveApplicationService.save(leaveApplication);

        return "redirect:/leave/personalHistory";
    }


    @GetMapping("view/{id}")
    public String viewLeave(Model model, @PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        model.addAttribute("leave", leaveApplication);
        return "leaveApplication/viewLeave";
    }

    //manager view details of his subordinate
    @GetMapping("viewSubordinateDetails/{id}")
    public String viewLeaveDetails(Model model, @PathVariable int id){
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "leaveApplication/managerViewLeaveDetails";
    }

    //manager can't view his subordinates leave applications history if i use "getLeaveApplicationIfBelongsToEmployee()"
    //so i create a new method
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


    //Add view subordinates history searching function
    @RequestMapping(value="searchingLeaveApplications")
    public String search(@RequestParam("keyword")
                         String k, @RequestParam("searchType") String t, Model
                                 model)
    {
        String name=new String("name");
        String id = new String("id");

        //have error here, should make sure these subordinates belong to the manager
        if(t.equals(name))
        {
            List<LeaveApplication> searchResults = leaveApplicationService.findByEmployeeName(k);
            model.addAttribute("subordinateLeaveApplications",
                    leaveApplicationService.getLeaveApplicationIfBelongsToManagerSubordinates(searchResults, authenticationService.getLoggedInEmployeeId()));
        }
        else if(t.equals(id))
        {
            int k_num = Integer.parseInt(k);
            List<LeaveApplication> searchResults = leaveApplicationService.findByEmployeeId(k_num);
            model.addAttribute("subordinateLeaveApplications",
                   leaveApplicationService.getLeaveApplicationIfBelongsToManagerSubordinates(searchResults, authenticationService.getLoggedInEmployeeId()));
        }
        else
        {
            return "redirect:404-notfound";
        }
        return "leaveApplication/managerViewLeave";
    }
}
