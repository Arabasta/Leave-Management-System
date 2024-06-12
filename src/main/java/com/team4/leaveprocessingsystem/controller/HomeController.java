package com.team4.leaveprocessingsystem.controller;

import ch.qos.logback.core.model.Model;
import com.team4.leaveprocessingsystem.service.RedirectService;
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
    public String employeeHome(Model model) {
        return "employee/home/home";
    }

    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        return "admin/home/home";
    }

    @GetMapping("/manager/home")
    public String managerHome(Model model) {
        return "manager/home/home";
    }
}