package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.service.auth.RedirectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final RedirectService redirectService;

    @Autowired
    public HomeController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @GetMapping({"/", "/home"})
    public String redirectHome() {
        return redirectService.getHomeRedirectUrl();
    }

    @GetMapping("/employee/home")
    public String employeeHome() {
        return "redirect:/employee/leave/create";
    }

    @GetMapping("/manager/home")
    public String managerHome() {
        return "redirect:/manager/reporting/viewCompensationClaims";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "redirect:/admin/manage-staff/";
    }
}