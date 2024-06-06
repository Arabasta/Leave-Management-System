package com.team4.leaveprocessingsystem.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EmployeeHomeController {

    @RequestMapping("/")
    public String getHomePage(Model model) {
            return "home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        return "home";
    }
}
