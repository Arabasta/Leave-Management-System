package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
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
        model.addAttribute("applicationType", "create");
        //TODO: Get all applied/updated/approved leave to prevent overlap

        return "leaveApplication/leaveForm";
    }

    @GetMapping("edit/{id}")
    public String editLeave(@PathVariable int id, Model model){
        model.addAttribute("leave", leaveService.findLeaveApplicationById(id));
        model.addAttribute("leaveTypes", LeaveTypeEnum.values());
        model.addAttribute("applicationType", "update");
        //TODO: Get all applied/updated/approved leave to prevent overlap

        return "leaveApplication/leaveForm";
    }

    @PostMapping("save")
    public String saveLeave(@Valid @ModelAttribute("leave") LeaveApplication leaveApplication, BindingResult bindingResult, Model model, @RequestParam("applicationType") String applicationType){
        Employee employee = employeeService.findByName("employee"); // Replace with session get employee obj
        leaveApplication.setSubmittingEmployee(employee);
        if (bindingResult.hasErrors()) {
            model.addAttribute("leave", leaveApplication);
            model.addAttribute("leaveTypes", LeaveTypeEnum.values());
            model.addAttribute("applicationType", applicationType);
            return "leaveApplication/leaveForm";
        }

        leaveApplication.setReviewingManager(employee.getManager());
        switch (applicationType) {
            case "create":
                leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);
                break;
            case "update":
                leaveApplication.setLeaveStatus(LeaveStatusEnum.UPDATED);
                break;
        }
        leaveService.save(leaveApplication);

        return "redirect:/leave/history";
    }

    @GetMapping("delete/{id}")
    public String deleteLeave(@PathVariable int id){
        LeaveApplication leaveApplication = leaveService.findLeaveApplicationById(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveService.save(leaveApplication);

        //TODO: Only applied/updated leave can be deleted

        return "redirect:/leave/history";
    }

    @GetMapping("cancel/{id}")
    public String cancelLeave(@PathVariable int id){
        LeaveApplication leaveApplication = leaveService.findLeaveApplicationById(id);
        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveService.save(leaveApplication);

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


}
