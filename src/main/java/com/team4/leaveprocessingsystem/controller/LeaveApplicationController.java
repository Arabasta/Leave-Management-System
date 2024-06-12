package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationNotFoundException;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import com.team4.leaveprocessingsystem.service.EmployeeService;
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
    public String subordinatesLeaveHistory(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Employee manager = user.getEmployee();
        int managerId = manager.getId();
        List<LeaveApplication> allLeavesbyManagerSubordinates = leaveApplicationService.findSubordinatesLeaveApplicationsByReviewingManager_Id(managerId);
        model.addAttribute("leaveApplications",allLeavesbyManagerSubordinates);

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
}
