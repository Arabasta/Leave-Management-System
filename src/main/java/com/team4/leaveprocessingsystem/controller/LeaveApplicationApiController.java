package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.util.EmailBuilderUtils;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RequestMapping("api/leave")
@CrossOrigin()
@RestController
public class LeaveApplicationApiController {

    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;
    private final LeaveApplicationValidator leaveApplicationValidator;
    private final AuthenticationService authenticationService;
    private final EmailApiService emailApiService;
    private final UserService userService;

    @InitBinder
    private void initLeaveApplicationApiBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @Autowired
    public LeaveApplicationApiController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService,
                                      AuthenticationService authenticationService, LeaveApplicationValidator leaveApplicationValidator,
                                      EmailApiService emailApiService, UserService userService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.authenticationService = authenticationService;
        this.leaveApplicationValidator = leaveApplicationValidator;
        this.emailApiService = emailApiService;
        this.userService = userService;
    }

    @PostMapping("create")
    public ResponseEntity<LeaveApplication> createLeave(@Valid @RequestBody LeaveApplication leaveApplication, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<> (HttpStatus.BAD_REQUEST);
        }

        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(employee.getManager());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);

        leaveApplicationService.save(leaveApplication);

//        // Send email notification to the manager
//        try {
//            String emailAdd = userService.findUserRolesByEmployeeId(leaveApplication.getReviewingManager().getId()).get(0).getEmail();
//            Map<String, String> email =  EmailBuilderUtils.buildNotificationEmail(leaveApplication);
//            emailApiService.sendEmail(emailAdd, email.get("subject"), email.get("text"));
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }

        return new ResponseEntity<> (leaveApplication, HttpStatus.CREATED);
    }

    @PutMapping("edit/{id}")
    public ResponseEntity<LeaveApplication> editLeave(@Valid @RequestBody LeaveApplication inleaveApplication, BindingResult bindingResult, @PathVariable int id){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<> (HttpStatus.BAD_REQUEST);
        }
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());

        // Only allow editing of leaves pending approval
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        leaveApplication.setLeaveType(inleaveApplication.getLeaveType());
        leaveApplication.setStartDate(inleaveApplication.getStartDate());
        leaveApplication.setEndDate(inleaveApplication.getEndDate());
        leaveApplication.setSubmissionReason(inleaveApplication.getSubmissionReason());
        leaveApplication.setWorkDissemination(inleaveApplication.getWorkDissemination());
        leaveApplication.setContactDetails(inleaveApplication.getContactDetails());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.UPDATED);
        leaveApplicationService.save(leaveApplication);

        return new ResponseEntity<> (leaveApplication, HttpStatus.OK);
    }

    @PutMapping("delete/{id}")
    public ResponseEntity<LeaveApplication> deleteLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        return new ResponseEntity<> (leaveApplication, HttpStatus.OK);
    }

    @PutMapping("cancel/{id}")
    public ResponseEntity<LeaveApplication> cancelLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());
        // Only approved leave can be cancelled
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPROVED) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        leaveApplication.setLeaveStatus(LeaveStatusEnum.CANCELLED);
        leaveApplicationService.save(leaveApplication);

        return new ResponseEntity<> (leaveApplication, HttpStatus.OK);
    }


    @GetMapping("view/{id}")
    public ResponseEntity<LeaveApplication> viewLeave(@PathVariable int id){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(id, employee.getId());

        return new ResponseEntity<> (leaveApplication, HttpStatus.OK);
    }
}
