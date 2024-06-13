package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveType;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.LeaveTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/manage-leave-types")
public class ManageLeaveTypesController {
    private final LeaveBalanceService leaveBalanceService;
    private final EmployeeService employeeService;
    private final LeaveTypeService leaveTypeService;
    private final JobDesignationService jobDesignationService;

    @Autowired
    public ManageLeaveTypesController(LeaveBalanceService leaveBalanceService,
                                      EmployeeService employeeService,
                                      LeaveTypeService leaveTypeService,
                                      JobDesignationService jobDesignationService) {
        this.leaveBalanceService = leaveBalanceService;
        this.employeeService = employeeService;
        this.leaveTypeService = leaveTypeService;
        this.jobDesignationService = jobDesignationService;
    }

    @GetMapping(value = "/")
    public String leaveTypeList(Model model) {
        List<LeaveType> leaveTypeList = leaveTypeService.findAllLeaveTypes();
        List<JobDesignation> jobDesignationList = jobDesignationService.listAllJobDesignations();
        model.addAttribute("leaveTypeList", leaveTypeList);
        model.addAttribute("jobDesignations", jobDesignationList);
        return "manage-leave-types/view-all";
    }

    @GetMapping(value = "/edit/{leaveTypeId}")
    public String leaveTypeEditForm(@PathVariable(name = "leaveTypeId") Integer leaveTypeId, Model model) {
        model.addAttribute("leaveType", leaveTypeService.findLeaveTypeById(leaveTypeId));
        model.addAttribute("isEditMode", true);
        return "manage-leave-types/edit-leave-type-form";
    }

    @PostMapping(value = "/update")
    public String leaveTypeEdit(@RequestParam("leaveTypeId") Integer leaveTypeId,
                                @Valid @ModelAttribute("leaveType") LeaveType leaveType,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("leaveType", leaveTypeService.findLeaveTypeById(leaveTypeId));
            return "admin-leave-type-create";
        }
        model.addAttribute("isEditMode", false);

        leaveTypeService.updateLeaveType(leaveType);
        return "redirect:/admin/manage-leave-types";
    }

    /*
    @GetMapping(value = "/leave_type/create")
    public String leaveTypeCreateForm(Model model) {
        model.addAttribute("leaveType", new LeaveType());
        return "admin-leave-type-create";
    }

    @PostMapping(value = "/leave_type/create")
    public String leaveTypeCreate(@Valid @ModelAttribute("leaveType") LeaveType leaveType, BindingResult bindingResult,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("leaveType", new LeaveType());
            return "admin-leave-type-create";
        }

        leaveTypeService.createLeaveType(leaveType);
        return "redirect:/admin/leave_type";
    }
     */

}
