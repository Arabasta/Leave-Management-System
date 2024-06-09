package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Setter
@Controller
public class AdminManageStaffController {
    @Autowired
    private IEmployee employeeService;

    @RequestMapping(value = "/search-staff")
    public String search(@RequestParam(value = "keyword", required = false) String k,
                         @RequestParam(value = "searchtype", required = false) String t,
                         Model model) {

        if (k == null)
            model.addAttribute("staffs", employeeService.listAllEmployees());
        if (t == null)
            t = "";

        // Todo: fix bug - no display of specific staff when filtered by name, job designation, and role
        switch (t){
            case (""):
                model.addAttribute("staffs", employeeService.listAllEmployees());
                break;
            case ("name"):
                model.addAttribute("staffs", employeeService.SearchEmployeeByName(k));
                break;
            case ("jobDesignation"):
                model.addAttribute("staffs", employeeService.findEmployeeByJobDesignation(k));
                break;
            case ("roleType"):
                model.addAttribute("staffs", employeeService.findUserByRoleType(k));
                break;
            default:
                return "errorStaffNotFound";
        }

//        if (t == null) {
//            model.addAttribute("staffs", employeeService.listAllEmployees());
//        } else if (t.equals(name)) {
//            model.addAttribute("staffs", employeeService.SearchEmployeeByName(k));
//        } else if (t.equals(jobDesignation)) {
//            model.addAttribute("staffs", employeeService.findEmployeeByJobDesignation(k));
//        } else if (t.equals(roleType)) {
//            model.addAttribute("staffs", employeeService.findUserByRoleType(k));
//        }
//        else return "errorStaffNotFound";

        model.addAttribute("keyword", k);
        model.addAttribute("searchtype", t);
        return "searchStaffResults";

    }


}
