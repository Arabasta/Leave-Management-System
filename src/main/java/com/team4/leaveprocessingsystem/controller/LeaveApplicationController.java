package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LeaveApplicationController {
    @Autowired
    private ILeaveApplication leaveService;

    @Autowired
    public void setLeaveService(LeaveApplicationService leaveService){
        this.leaveService = leaveService;
    }

    @GetMapping("/leave/create")
    public String createLeave(Model model){
        LeaveApplication leaveApplication = new LeaveApplication();
        model.addAttribute(("leave"), leaveApplication);

        //TODO: Get all applied/updated/approved leave to prevent overlap

        return "leaveApplication/createLeave";
    }

    @PostMapping("/leave/create/save")
    public String saveCreatedLeave(@ModelAttribute("leave") LeaveApplication leaveApplication){
//        if (bindingResult.hasErrors()) { //to implement validation
//            return "leaveApplication/createLeave";
//        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);
        leaveService.save(leaveApplication);
        return "leaveApplication/viewLeaveHistory";
    }

    @GetMapping("/leave/edit/{id}")
    public String editLeave(@PathVariable int id, Model model){
        model.addAttribute("leave", leaveService.findLeaveApplicationById(id));
        return "leaveApplication/editLeave";
    }

    @GetMapping("/leave/edit/save")
    public String saveEditedLeave(@ModelAttribute("leave") LeaveApplication leaveApplication){
//        if (bindingResult.hasErrors()) { //to implement validation
//            return "leaveApplication/editLeave";
//        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.UPDATED);
        leaveService.save(leaveApplication);
        return "leaveApplication/viewLeaveHistory";
    }

    @GetMapping("leave/delete/{id}")
    public String deleteLeave(@PathVariable int id, Model model){
        model.addAttribute("leave", leaveService.findLeaveApplicationById(id));
        return "leaveApplication/deleteLeave";
    }

    @GetMapping("leave/delete/confirm")
    public String deleteConfirmLeave(@ModelAttribute("leave") LeaveApplication leaveApplication){
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveService.save(leaveApplication);
        return "leaveApplication/viewLeaveHistory";
    }

    //TODO: Change status from APPROVED to CANCELLED

}
