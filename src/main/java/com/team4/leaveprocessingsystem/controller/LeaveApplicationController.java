package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
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
    private LeaveApplicationValidator leaveApplicationValidator;

    @InitBinder
    private void initLeaveApplicationBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @GetMapping("create")
    public String createLeave(Model model){
        LeaveApplication leaveApplication = new LeaveApplication();
        Employee employee = employeeService.findByName("employee"); // Replace with session get employee obj
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
        Employee employee = employeeService.findByName("employee"); // Replace with session get employee obj
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
        Employee employee = employeeService.findByName("employee"); // Replace with session get employee obj
        List<LeaveApplication> allLeaves = leaveApplicationService.findBySubmittingEmployee(employee);
        model.addAttribute("leaveApplications", allLeaves);

        return "leaveApplication/viewLeaveHistory";
    }
}
