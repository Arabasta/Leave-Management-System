package com.team4.leaveprocessingsystem.controller.employee;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationUpdateException;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.util.EmailBuilderUtils;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping("employee/leave")
@Controller
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;
    private final LeaveApplicationValidator leaveApplicationValidator;
    private final AuthenticationService authenticationService;
    private final EmailApiService emailApiService;
    private final UserService userService;
    private final PublicHolidayService publicHolidayService;

    @InitBinder("leave")
    private void initLeaveApplicationBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @Autowired
    public LeaveApplicationController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService,
                                      AuthenticationService authenticationService, LeaveApplicationValidator leaveApplicationValidator,
                                      EmailApiService emailApiService, UserService userService, PublicHolidayService publicHolidayService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.authenticationService = authenticationService;
        this.leaveApplicationValidator = leaveApplicationValidator;
        this.emailApiService = emailApiService;
        this.userService = userService;
        this.publicHolidayService = publicHolidayService;
    }

    @GetMapping("create")
    public String createLeave(Model model) {
        LeaveApplication leaveApplication = new LeaveApplication();

        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(employee.getManager());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);

        model.addAttribute("publicHolidays", publicHolidayService.publicHolidayDateList());
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
        model.addAttribute("publicHolidays", publicHolidayService.publicHolidayDateList());
        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());

        return "employee/leave-application/leaveForm";
    }

    @PostMapping("save")
    public String saveLeave(@Valid @ModelAttribute("leave") LeaveApplication leaveApplication, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("publicHolidays", publicHolidayService.publicHolidayDateList());
            model.addAttribute("leave", leaveApplication);
            model.addAttribute("leaveTypes", LeaveTypeEnum.values());
            return "employee/leave-application/leaveForm";
        }
        leaveApplicationService.save(leaveApplication);

        // uncomment before submitting due to limit
        // Send email notification to the manager
//        if (leaveApplication.getReviewingManager() != null){
//            try {
//                String emailAdd = userService.findUserRolesByEmployeeId(leaveApplication.getReviewingManager().getId()).get(0).getEmail();
//                Map<String, String> email = EmailBuilderUtils.buildNotificationEmail(leaveApplication);
//                emailApiService.sendEmail(emailAdd, email.get("subject"), email.get("text"));
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//        }


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
    public String personalHistory(Model model,
                                  @RequestParam(defaultValue = "0") Integer pageNo,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Employee loginEmployee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<LeaveApplication> personalLeaveApplicationsPage = leaveApplicationService.findBySubmittingEmployeeWithPaging(loginEmployee, pageable);

        model.addAttribute("personalLeaveApplications", personalLeaveApplicationsPage.getContent());
        model.addAttribute("page", personalLeaveApplicationsPage);
        LeaveBalance leaveBalance = loginEmployee.getLeaveBalance();
        model.addAttribute("LeaveBalance", leaveBalance);
        return "employee/leave-application/personalViewLeave";
    }
}
