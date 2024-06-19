package com.team4.leaveprocessingsystem.controller.diagram;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiagramController {

    @GetMapping("/diagram")
    public String diagram() {
        return "diagram/diagram";
    }
}
