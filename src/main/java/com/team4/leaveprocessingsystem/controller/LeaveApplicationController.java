package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("leave")
@Controller
public class LeaveApplicationController {
    @Autowired
    private ILeaveApplication leaveService;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    public void setLeaveService(LeaveApplicationService leaveService){
        this.leaveService = leaveService;
    }

    // Pending PR merge
//    @Autowired
//    private LeaveApplicationValidator leaveApplicationValidator;
//
//    @InitBinder("course")
//    private void initLeaveApplicationBinder(WebDataBinder binder) {
//        binder.addValidators(leaveApplicationValidator);
//    }

    @GetMapping("create")
    public String createLeave(Model model){
        LeaveApplication leaveApplication = new LeaveApplication();
        Employee employee = employeeService.findByName("employee"); // Replace with session get employee obj
        leaveApplication.setSubmittingEmployee(employee);

        model.addAttribute("leave", leaveApplication);
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());
        //TODO: Get all applied/updated/approved leave to prevent overlap

        return "leaveApplication/leaveForm";
    }

    @PostMapping("create")
    public String saveCreatedLeave(@Valid @ModelAttribute("leave") LeaveApplication leaveApplication, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "leaveApplication/leaveForm";
        }
        Employee employee = employeeService.findByName("employee"); // Replace with session get employee obj
        Manager manager = employee.getManager();

        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(manager);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);
        leaveService.save(leaveApplication);

        return "leaveApplication/viewLeaveHistory";
    }

    @GetMapping("edit/{id}")
    public String editLeave(@PathVariable int id, Model model){
        model.addAttribute("leave", leaveService.findLeaveApplicationById(id));

        //TODO: Get all applied/updated/approved leave to prevent overlap

        return "leaveApplication/leaveForm";
    }

    @PostMapping("edit/{id}")
    public String saveEditedLeave(@Valid @ModelAttribute("leave") LeaveApplication leaveApplication, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "leaveApplication/leaveForm";
        }

        //TODO: Applied/Updated leave can be updated

        leaveApplication.setLeaveStatus(LeaveStatusEnum.UPDATED);
        leaveService.save(leaveApplication);
        return "leaveApplication/viewLeaveHistory";
    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id){
        LeaveApplication leaveApplication = leaveService.findLeaveApplicationById(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveService.save(leaveApplication);

        //TODO: Applied/updated leave can be deleted

        return "leaveApplication/viewLeaveHistory";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id){
        LeaveApplication leaveApplication = leaveService.findLeaveApplicationById(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveService.save(leaveApplication);

        //TODO: Applied/updated/approved leave can be cancelled

        return "leaveApplication/viewLeaveHistory";
    }
}
