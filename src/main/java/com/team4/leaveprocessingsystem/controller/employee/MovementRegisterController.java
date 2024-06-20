package com.team4.leaveprocessingsystem.controller.employee;

import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.service.LeaveApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequestMapping("/employee/movement-register")
@Controller
public class MovementRegisterController {

    private final LeaveApplicationService leaveApplicationService;

    public MovementRegisterController(LeaveApplicationService leaveApplicationService) {
        this.leaveApplicationService = leaveApplicationService;
    }

    @RequestMapping("view")
    public String viewMovementRegister(@RequestParam(value = "date", required = false) String date,
                                       Model model) {
        Map<String, List<LeaveApplication>> applicationsMap = leaveApplicationService.mapEmployeeOnLeave(date);
        model.addAttribute("applicationsMap", applicationsMap);
        model.addAttribute("pickedDate", date);
        return "employee/movement-register/view";
    }
}
