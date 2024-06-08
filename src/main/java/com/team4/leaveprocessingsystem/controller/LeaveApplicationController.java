package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LeaveApplicationController {
    @Autowired
    private ILeaveApplication leaveService;

    @Autowired
    public void setLeaveService(LeaveApplicationService leaveService){
        this.leaveService = leaveService;
    }

    @GetMapping("/leave/create")
    public String createLeave(){
        return "createLeave";
    }

    @PostMapping("/leave/create/save")
    public String saveCreatedLeave(){
        return "viewLeaveHistory";
    }

    @GetMapping("/leave/edit/{id}")
    public String editLeave(@PathVariable int id, Model model){
        model.addAttribute("leave", leaveService.findLeaveApplicationById(id));
        return "editLeave";
    }

    @GetMapping("/leave/edit/save")
    public String saveEditedLeave(){
        return "viewLeaveHistory";
    }

    @GetMapping("leave/delete/{id}")
    public String deleteLeave(@PathVariable int id, Model model){
        model.addAttribute("leave", leaveService.findLeaveApplicationById(id));
        System.out.println(leaveService.findLeaveApplicationById(id));
        return "viewLeaveHistory";
    }

    @GetMapping("leave/delete/confirm")
    public String deleteConfirmLeave(){
        return "viewLeaveHistory";
    }

}
