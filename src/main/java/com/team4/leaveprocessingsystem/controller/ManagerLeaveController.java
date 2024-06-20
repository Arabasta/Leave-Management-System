package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.util.EmailBuilderUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequestMapping("manager/leave")
@Controller
public class ManagerLeaveController {

    private final EmployeeService employeeService;
    private final AuthenticationService authenticationService;
    private final EmailApiService emailApiService;
    private final UserService userService;
    private final LeaveApplicationService leaveApplicationService;
    private final ManagerService managerService;
    private final LeaveBalanceService leaveBalanceService;
    private final ReportingService reportingService;

    public ManagerLeaveController(EmployeeService employeeService, AuthenticationService authenticationService,
                                  EmailApiService emailApiService, UserService userService,
                                  LeaveApplicationService leaveApplicationService, ManagerService managerService,
                                  LeaveBalanceService leaveBalanceService, ReportingService reportingService) {
        this.employeeService = employeeService;
        this.authenticationService = authenticationService;
        this.emailApiService = emailApiService;
        this.userService = userService;
        this.leaveApplicationService = leaveApplicationService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
        this.reportingService = reportingService;
    }

    // TODO: implement validators for below parameters
    @RequestMapping(value="managerView")
    public String viewLeaveApplications(@RequestParam(value="keyword", required = false) String keyword,
                                        @RequestParam(value="searchType", required = false) String searchType,
                                        @RequestParam(value="startDate", required = false) String startDate,
                                        @RequestParam(value="endDate", required = false) String endDate,
                                        @RequestParam(value="leaveStatus", required = false, defaultValue = "ALL") String leaveStatus,
                                        @RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        Model model) {
        int managerId = authenticationService.getLoggedInEmployeeId();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<LeaveApplication> applications = leaveApplicationService
                .filterManagerViewSearch(managerId, keyword, searchType, startDate, endDate, leaveStatus, pageable);

        model.addAttribute("reportingDTO",reportingService
                .setForLeavesReport(leaveApplicationService.setArrayList(applications.getContent())));
        model.addAttribute("page", applications);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("leaveStatus", leaveStatus);
        return "manager/leave-application/managerViewLeave";
    }


    // MANAGER - GET - PENDING LEAVE APPLICATIONS
    @GetMapping("pendingLeaves")
    public String pendingLeaveApplications(Model model) {
        Manager currentManager = (Manager) employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<LeaveApplication>> pendingLeaveApplications = leaveApplicationService.findLeaveApplicationsPendingApprovalByManager(currentManager);
        model.addAttribute("pendingLeaveApplications", pendingLeaveApplications);
        return "manager/leave-application/pendingLeaveApplications";
    }

    //manager view details of his subordinate
    @GetMapping("viewSubordinateDetails/{id}")
    public String viewLeaveDetails(Model model, @PathVariable int id) {
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "manager/leave-application/managerViewLeaveDetails";
    }

    // MANAGER - GET - REVIEW LEAVE APPLICATIONS DETAILS
    @GetMapping("review/{id}")
    public String leaveApplicationsDetails(@PathVariable Integer id, Model model) {
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "manager/leave-application/reviewLeave";
    }

    // MANAGER - POST - REVIEW LEAVE APPLICATION
    @PostMapping("/submitLeaveApplication")
    public String reviewLeaveApplication(@Valid @ModelAttribute("leave") LeaveApplication leave,
                                         BindingResult bindingResult) {
        LeaveApplication existingLeaveApplication = leaveApplicationService.findLeaveApplicationById(leave.getId());
        existingLeaveApplication.setSubmittingEmployee(employeeService.findEmployeeById(leave.getSubmittingEmployee().getId()));
        existingLeaveApplication.setReviewingManager(managerService.findManagerById(leave.getReviewingManager().getId()));
        existingLeaveApplication.setLeaveStatus(leave.getLeaveStatus());
        existingLeaveApplication.setLeaveType(leave.getLeaveType());
        existingLeaveApplication.setStartDate(leave.getStartDate());
        existingLeaveApplication.setEndDate(leave.getEndDate());
        existingLeaveApplication.setSubmissionReason(leave.getSubmissionReason());
        existingLeaveApplication.setReviewingManager(leave.getReviewingManager());
        existingLeaveApplication.setWorkDissemination(leave.getWorkDissemination());
        existingLeaveApplication.setContactDetails(leave.getContactDetails());
        existingLeaveApplication.setRejectionReason(leave.getRejectionReason());


        // Return back to page if validation has errors
        if (bindingResult.hasErrors()) {
            return "manager/leave-application/reviewLeave";
        }

        // Check if the leave is rejected and ensure the rejection reason is provided
        if (leave.getLeaveStatus() == LeaveStatusEnum.REJECTED && (leave.getRejectionReason() == null || leave.getRejectionReason().trim().isEmpty())) {
            bindingResult.rejectValue("rejectionReason", "error.leave", "Rejection reason must be provided if the leave is rejected");
            return "manager/leave-application/reviewLeave";
        }

        // Update to Approved
        if (leave.getLeaveStatus() == LeaveStatusEnum.APPROVED){
            existingLeaveApplication.setLeaveStatus(LeaveStatusEnum.APPROVED);
        }

        //Update to Rejected
        if (leave.getLeaveStatus() == LeaveStatusEnum.REJECTED){
            existingLeaveApplication.setLeaveStatus(LeaveStatusEnum.REJECTED);
        }
        leaveApplicationService.save(existingLeaveApplication);
        leaveBalanceService.update(existingLeaveApplication);

        // TODO: uncomment before submitting due to limit
        // Send email notification to the employee
//        try {
//            String emailAdd = userService.findUserRolesByEmployeeId(existingLeaveApplication.getSubmittingEmployee().getId()).get(0).getEmail();
//            Map<String, String> email = EmailBuilderUtils.buildNotificationEmail(existingLeaveApplication);
//            emailApiService.sendEmail(emailAdd, email.get("subject"), email.get("text"));
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }

        // Redirect to pending leave applications with a success parameter
        return "redirect:/manager/leave/pendingLeaves?updateSuccess=true";
    }
}
