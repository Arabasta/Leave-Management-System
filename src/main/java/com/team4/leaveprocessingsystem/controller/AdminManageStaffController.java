package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Setter
@Controller
public class AdminManageStaffController {
    @Autowired
    private IEmployee employeeService;

    @RequestMapping("/see-staff")
    public String getStaffPage(Model model) {
        return "searchStaff";
    }

    @RequestMapping(value = "/all/staff/searching")
    public String search(@RequestParam("keyword") String k,
                         @RequestParam("searchtype") String t,
                         Model model) {
        String name = new String("name");
        String jobDesignation = new String("jobDesignation");
        String roleType = new String("roleType");

        if (t.equals(name)) model.addAttribute("staffs", employeeService.SearchEmployeeByName(k));
            // TODO: fix query strings
            else if (t.equals(jobDesignation)) model.addAttribute("staffs", employeeService.findEmployeeByJobDesignation(k));
            //else if (t.equals(roleType)) model.addAttribute("staffs", employeeService.findUserByRoleType(roleType));
        else return "errorStaffNotFound";
        return "searchStaffResults";
    }


}
