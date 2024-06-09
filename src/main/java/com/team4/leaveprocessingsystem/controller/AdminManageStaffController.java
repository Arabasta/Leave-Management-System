package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Setter
@Controller
public class AdminManageStaffController {
    @Autowired
    private IEmployee employeeService;

    @RequestMapping(value = "/search-staff")
    public String search(@Param("keyword") String k,
                         @Param("searchtype") String t,
                         Model model) {

        String name = "name";
        String jobDesignation = "jobDesignation";
        String roleType = "roleType";

        // Todo: fix bug - no display of specific staff when filtered by name, job designation, and role
        if (t == null) {
            model.addAttribute("staffs", employeeService.listAllEmployees());
        } else if (t.equals(name)) {
            model.addAttribute("staffs", employeeService.SearchEmployeeByName(k));
        } else if (t.equals(jobDesignation)) {
            model.addAttribute("staffs", employeeService.findEmployeeByJobDesignation(k));
        } else if (t.equals(roleType)) {
            model.addAttribute("staffs", employeeService.findUserByRoleType(k));
        }
        else return "errorStaffNotFound";

        model.addAttribute("keyword", k);
        return "searchStaffResults";

    }


}
