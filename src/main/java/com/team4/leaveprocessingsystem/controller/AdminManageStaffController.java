package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;

import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Controller
public class AdminManageStaffController {
    @Autowired
    private IEmployee employeeService;
    @Autowired
    private JobDesignationService jobDesignationService;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/search-staff")
    public String search(@RequestParam(value="keyword", required=false) String k,
                         @RequestParam(value="searchtype", required=false) String t,
                         Model model) {

        String name = "name";
        String jobDesignation = "jobDesignation";
        String roleType = "roleType";

        // Todo:
        if (k == null) model.addAttribute("staffs", employeeService.listAllEmployees());
        if (t == null) t = "";

        switch (t) {
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
        model.addAttribute("keyword", k);
        model.addAttribute("searchtype", t);
        return "searchStaffResults";
    }

    @RequestMapping(value="edit/{id}")
    public ModelAndView showEditStaffForm(@PathVariable(name="id") int id, Model model) {
        //model.addAttribute("staffs", employeeService.listAllEmployees());
        //model.addAttribute("roles", RoleEnum.values());
        //model.addAttribute("jobdesignations", jobDesignationService.listAllJobDesignations());

        // todo: checkboxes for role, to find a way to check if the role == that users' role(s)
        boolean employeeWithUserRoleFlag = true;

        // compare between the role(s) of a specific employee and roleEnum and see which matches

        List<User> employeeUserAccount = userRepository.findUserRolesByEmployeeId(id);

        List<String> roleEnumNames = Stream.of(RoleEnum.values())
                .map(RoleEnum::name)
                .collect(Collectors.toList());



        model.addAttribute("flag", employeeWithUserRoleFlag);

        ModelAndView mav = new ModelAndView("editStaff");
        Employee employee = employeeService.findEmployeeById(id);

        mav.addObject("employee", employee);
        mav.addObject("jobdesignations", jobDesignationService.listAllJobDesignations());



        mav.addObject("roles", roleEnumNames);
        mav.addObject("users", userRepository.findUserRolesByEmployeeId(id));
        return mav;
    }

}
