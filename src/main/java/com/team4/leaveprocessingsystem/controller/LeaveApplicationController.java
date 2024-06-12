package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.AuthenticationService;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// todo: add and refactor into a LeaveHistoryController
@RequestMapping("leave")
@Controller
public class LeaveApplicationController {
    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;
    private final LeaveBalanceService leaveBalanceService;
    private final LeaveApplicationValidator leaveApplicationValidator;
    private final AuthenticationService authenticationService;

    @InitBinder
    private void initLeaveApplicationBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @Autowired
    public LeaveApplicationController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService,
                                      LeaveBalanceService leaveBalanceService, AuthenticationService authenticationService, LeaveApplicationValidator leaveApplicationValidator) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.leaveBalanceService = leaveBalanceService;
        this.authenticationService = authenticationService;
        this.leaveApplicationValidator = leaveApplicationValidator;
    }

    @GetMapping("create")
    public String createLeave(Model model){
        LeaveApplication leaveApplication = new LeaveApplication();
        // todo: note; kei changed to use authService

        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId()); // Replace with session get employee obj
        leaveApplication.setSubmittingEmployee(employee);

        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());
        model.addAttribute("applicationStatus", "APPLIED");
        //TODO: Get all applied/updated/approved leave to prevent overlap

        return "leaveApplication/leaveForm";
    }

    @GetMapping("edit/{id}")
    public String editLeave(@PathVariable int id, Model model){
        model.addAttribute("leave", leaveApplicationService.findLeaveApplicationById(id));
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());
        model.addAttribute("applicationStatus", "UPDATED");
        //TODO: Get all applied/updated/approved leave to prevent overlap

        return "leaveApplication/leaveForm";
    }

    @PostMapping("save")
    public String saveLeave(@Valid @ModelAttribute("leave") LeaveApplication leaveApplication, BindingResult bindingResult, Model model, @RequestParam("applicationStatus") String applicationStatus){
        // todo: note; kei changed to use authService
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId()); // Replace with session get employee obj
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(employee.getManager());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.valueOf(applicationStatus));
        if (bindingResult.hasErrors()) {
            model.addAttribute("leave", leaveApplication);
            model.addAttribute("leaveTypes", LeaveTypeEnum.values());
            model.addAttribute("applicationStatus", applicationStatus);
            return "leaveApplication/leaveForm";
        }

        // only update if leave is approved
        // leaveBalanceService.update(leaveApplication);
        leaveApplicationService.save(leaveApplication);

        return "redirect:/leave/history";
    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id){
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only applied/updated leave can be deleted

        return "redirect:/leave/history";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id){
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only approved leave can be cancelled

        return "redirect:/leave/history";
    }

    @GetMapping("history")
    public String leaveHistory(Model model){
        // todo: note; kei changed to use authService
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId()); // Replace with session get employee obj
        List<LeaveApplication> allLeaves = leaveApplicationService.findBySubmittingEmployee(employee);
        model.addAttribute("leaveApplications", allLeaves);

        return "leaveApplication/viewLeaveHistory";
    }
}
