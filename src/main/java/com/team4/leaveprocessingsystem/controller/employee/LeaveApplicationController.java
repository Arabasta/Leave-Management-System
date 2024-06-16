package com.team4.leaveprocessingsystem.controller.employee;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationUpdateException;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
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

@RequestMapping("employee/leave")
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
    public String createLeave(Model model) {
        LeaveApplication leaveApplication = new LeaveApplication();
        // todo: note; kei changed to use authService

        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(employee.getManager());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);

        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());

        return "employee/leave-application/leaveForm";
    }

    @GetMapping("edit/{id}")
    public String editLeave(@PathVariable int id, Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());

        // Only allow editing of leaves pending approval
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED) {
            throw new LeaveApplicationUpdateException("Leave application cannot be edited");
        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.UPDATED);
        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());

        return "employee/leave-application/leaveForm";
    }

    @PostMapping("save")
    public String saveLeave(@Valid @ModelAttribute("leave") LeaveApplication leaveApplication, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("leave", leaveApplication);
            model.addAttribute("leaveTypes", LeaveTypeEnum.values());
            return "employee/leave-application/leaveForm";
        }
        leaveApplicationService.save(leaveApplication);

        // Send email notification to the manager
        if (leaveApplication.getReviewingManager() != null){
            try {
                String emailAdd = userService.findUserRolesByEmployeeId(leaveApplication.getReviewingManager().getId()).get(0).getEmail();
                Map<String, String> email = EmailBuilderUtils.buildNotificationEmail(leaveApplication);
                emailApiService.sendEmail(emailAdd, email.get("subject"), email.get("text"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }


        return "redirect:/employee/leave/personalHistory";

    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        // Only applied/updated leave can be deleted
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED) {
            throw new LeaveApplicationUpdateException("Leave application cannot be deleted");
        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        return "redirect:/employee/leave/personalHistory";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        // Only approved leave can be cancelled
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPROVED) {
            throw new LeaveApplicationUpdateException("Leave application cannot be cancelled");
        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveApplicationService.save(leaveApplication);

        return "redirect:/employee/leave/personalHistory";
    }

    @GetMapping("view/{id}")
    public String viewLeave(Model model, @PathVariable int id) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        model.addAttribute("leave", leaveApplication);
        return "employee/leave-application/viewLeave";
    }

        @GetMapping("personalHistory")
    public String personalHistory(Model model) {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        List<LeaveApplication> personalLeaveApplications = leaveApplicationService.findBySubmittingEmployee(employee);
        model.addAttribute("personalLeaveApplications", personalLeaveApplications);
        return "employee/leave-application/personalViewLeave";
    }
}
