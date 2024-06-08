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

    @GetMapping("/home/employee")
    public String employeeHome(Model model) {
        return "home/employee";
    }

    @GetMapping("/home/admin")
    public String adminHome(Model model) {
        return "home/admin";
    }

    @GetMapping("/home/manager")
    public String managerHome(Model model) {
        return "home/manager";
    }
}