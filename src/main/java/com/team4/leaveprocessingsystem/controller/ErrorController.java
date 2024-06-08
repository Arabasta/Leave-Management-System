package com.team4.leaveprocessingsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ErrorController {

    @GetMapping("403-forbidden")
    public String accessDenied() {
        return "error/403-forbidden";
    }
}
