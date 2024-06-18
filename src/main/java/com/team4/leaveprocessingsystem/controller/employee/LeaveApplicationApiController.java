package com.team4.leaveprocessingsystem.controller.employee;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.dataTransferObjects.LeaveApplicationResponse;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.util.EmailBuilderUtils;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("api/employee/leave")
@CrossOrigin
@RestController
public class LeaveApplicationApiController {

    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;
    private final LeaveApplicationValidator leaveApplicationValidator;
    private final AuthenticationService authenticationService;
    private final EmailApiService emailApiService;
    private final UserService userService;
    private final LeaveBalanceService leaveBalanceService;

    @InitBinder
    private void initLeaveApplicationApiBinder(WebDataBinder binder) {
        binder.addValidators(leaveApplicationValidator);
    }

    @Autowired
    public LeaveApplicationApiController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService,
                                      AuthenticationService authenticationService, LeaveApplicationValidator leaveApplicationValidator,
                                      EmailApiService emailApiService, UserService userService, LeaveBalanceService leaveBalanceService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
        this.authenticationService = authenticationService;
        this.leaveApplicationValidator = leaveApplicationValidator;
        this.emailApiService = emailApiService;
        this.userService = userService;
        this.leaveBalanceService = leaveBalanceService;
    }

    @PostMapping("create")
    public ResponseEntity<?> createLeave(@Valid @RequestBody LeaveApplication leaveApplication, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().stream().forEach(x -> {
                if (!errors.containsKey(x.getField())) {
                    errors.put(x.getField(), x.getDefaultMessage());
                }
            });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        leaveApplication.setSubmittingEmployee(employee);
        leaveApplication.setReviewingManager(employee.getManager());
        leaveApplication.setLeaveStatus(LeaveStatusEnum.APPLIED);

        leaveApplicationService.save(leaveApplication);

        // uncomment before submitting due to limit
        // Send email notification to the manager
//        if (leaveApplication.getReviewingManager() != null){
//            try {
//                String emailAdd = userService.findUserRolesByEmployeeId(leaveApplication.getReviewingManager().getId()).get(0).getEmail();
//                Map<String, String> email = EmailBuilderUtils.buildNotificationEmail(leaveApplication);
//                emailApiService.sendEmail(emailAdd, email.get("subject"), email.get("text"));
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//        }

        return new ResponseEntity<> (leaveApplication, HttpStatus.CREATED);
    }

    @PutMapping("edit")
    public ResponseEntity<?> editLeave(@Valid @RequestBody LeaveApplication inleaveApplication, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().stream().forEach(x -> {
                if (!errors.containsKey(x.getField())) {
                    errors.put(x.getField(), x.getDefaultMessage());
                }
            });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(inleaveApplication.getId(), employee.getId());

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

    @PutMapping("delete")
    public ResponseEntity<LeaveApplication> deleteLeave(@RequestBody LeaveApplication inleaveApplication){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(inleaveApplication.getId(), employee.getId());
        // Only applied or updated leave can be deleted
        if (leaveApplication.getLeaveStatus() != LeaveStatusEnum.APPLIED && leaveApplication.getLeaveStatus() != LeaveStatusEnum.UPDATED){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        leaveApplication.setLeaveStatus(LeaveStatusEnum.DELETED);
        leaveApplicationService.save(leaveApplication);

        return new ResponseEntity<> (leaveApplication, HttpStatus.OK);
    }

    @PutMapping("cancel")
    public ResponseEntity<LeaveApplication> cancelLeave(@RequestBody LeaveApplication inleaveApplication){
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplicationIfBelongsToEmployee(inleaveApplication.getId(), employee.getId());
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

    @GetMapping("personalHistory")
    public ResponseEntity<Object> personalHistory() {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        List<LeaveApplication> personalLeaveApplications = leaveApplicationService.findBySubmittingEmployee(employee);
        LeaveBalance leaveBalance = leaveBalanceService.findByEmployee(employee.getId());

        LeaveApplicationResponse response = new LeaveApplicationResponse(employee, personalLeaveApplications, leaveBalance);

        return new ResponseEntity<> (response, HttpStatus.OK);
    }
}
