package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationNotFoundException;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.PublicHolidayService;
import com.team4.leaveprocessingsystem.util.DateTimeCounterUtils;
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

import java.util.List;

@RequestMapping("leave")
@Controller
public class LeaveApplicationController {
    @Autowired
    private LeaveApplicationService leaveApplicationService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private LeaveBalanceService leaveBalanceService;
    @Autowired
    private PublicHolidayService    publicHolidayService;
    @Autowired
    private LeaveApplicationValidator leaveApplicationValidator;

    @InitBinder
    private void initLeaveApplicationBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @GetMapping("create")
    public String createLeave(Model model){
        // Get the user object that is logged in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user =(User) authentication.getPrincipal();
        Employee employee = user.getEmployee();

        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(employee.getManager());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);

        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());

        return "leaveApplication/leaveForm";
    }

    @GetMapping("edit/{id}")
    public String editLeave(@PathVariable int id, Model model){
        LeaveApplication leaveApplication = getLeaveApplicationIfBelongsToEmployee(id);
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

        // only update if leave is approved
        // leaveBalanceService.update(leaveApplication);
        leaveApplicationService.save(leaveApplication);

        return "redirect:/leave/history";
    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id){
        LeaveApplication leaveApplication = getLeaveApplicationIfBelongsToEmployee(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only applied/updated leave can be deleted

        return "redirect:/leave/history";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id){
        LeaveApplication leaveApplication = getLeaveApplicationIfBelongsToEmployee(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveApplicationService.save(leaveApplication);

        //TODO: Only approved leave can be cancelled

        return "redirect:/leave/history";
    }

    @GetMapping("history")
    public String leaveHistory(Model model){
        Employee employee = employeeService.findByName("employee"); // Replace with session get employee obj
        List<LeaveApplication> allLeaves = employee.getLeaveApplications();
        model.addAttribute("leaveApplications", allLeaves);

        return "leaveApplication/viewLeaveHistory";
    }

    @GetMapping("view/{id}")
    public String viewLeave(Model model, @PathVariable int id){
        LeaveApplication leaveApplication = getLeaveApplicationIfBelongsToEmployee(id);
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
            if (leaveApplication.getSubmittingEmployee().getId().equals(employee.getId())){
                throw new LeaveApplicationNotFoundException("Leave Application Not Found");
            }
        return leaveApplication;
    }
}
