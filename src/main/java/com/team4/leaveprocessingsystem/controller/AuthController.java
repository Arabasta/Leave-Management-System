package com.team4.leaveprocessingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // IMPORTANT: do not change route
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/auth/failure")
    public String loginFailure() {
        return "auth/failure";
    }
}
