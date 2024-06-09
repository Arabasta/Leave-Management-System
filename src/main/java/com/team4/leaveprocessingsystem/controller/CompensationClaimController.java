package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.exception.CompensationClaimNotFoundException;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.UserService;
import com.team4.leaveprocessingsystem.validator.CompensationClaimValidator;
import org.springframework.ui.Model;
import com.team4.leaveprocessingsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
public class CompensationClaimController {

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private CompensationClaimService compensationClaimService;

    @Autowired
    private CompensationClaimValidator compensationClaimValidator;

    @InitBinder("course")
    private void initCourseBinder(WebDataBinder binder) {
        binder.addValidators(compensationClaimValidator);
    }

    @ModelAttribute
    @RequestMapping("compensation-claims/history")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String viewCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        Employee currentEmployee = currentUser.getEmployee();
        model.addAttribute("isAdmin", currentUser.getRole()==RoleEnum.ROLE_ADMIN);
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaims", (currentEmployee != null ? currentEmployee.getCompensationClaims() : null));
        model.addAttribute("compensationClaimService", compensationClaimService);
        model.addAttribute("leaveBalanceService", leaveBalanceService);
        return "compensation-claims/history";
    }

    @RequestMapping(value = "compensation-claims/withdraw/{id}")
    public String deleteCompensationClaim(Model model, @PathVariable Integer id) throws CompensationClaimNotFoundException {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);

        compensationClaim.setCompensationClaimStatus(CompensationClaimStatusEnum.WITHDRAWN);
        compensationClaimService.changeCompensationClaim(compensationClaim);

        String message = "Compensation Claim " + compensationClaim.getId() + " was successfully withdrawn.";
        model.addAttribute("withdrawn_message",message);

        return "redirect:/compensation-claims/history";
    }

    @GetMapping("compensation-claims/edit/{id}")
    public String editCompensationClaim(@PathVariable Integer id, Model model) {
        CompensationClaim compensationClaim = compensationClaimService.findCompensationClaim(id);
        model.addAttribute("compensationClaim", compensationClaim);

        return "compensation-claims/edit";
    }

//    @PostMapping("/course/edit/{id}")
//    public String editCourse(@ModelAttribute @Valid CompensationClaim course, BindingResult result, @PathVariable Integer id,
//                             HttpSession session) throws CourseNotFound {
//        if (result.hasErrors())
//            return "course-edit";
//
//        System.out.println("DATES****" + course.getFromDate() + course.getToDate());
//
//        UserSession usession = (UserSession) session.getAttribute("usession");
//        course.setEmployeeId(usession.getEmployee().getEmployeeId());
//        course.setStatus(CourseEventEnum.UPDATED);
//
//        courseService.changeCourse(course);
//
//        return "redirect:/staff/course/history";
//    }

    @ModelAttribute
    @GetMapping("compensation-claims/create")
    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
    // TODO: refactor using Sessions after it is implemented
    public String createCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        Employee currentEmployee = currentUser.getEmployee();
        model.addAttribute("isAdmin", currentUser.getRole()==RoleEnum.ROLE_ADMIN);
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("compensationClaimService", compensationClaimService);
        model.addAttribute("compensationClaim", new CompensationClaim());
        return "compensation-claims/create";
    }

//    ref: https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html#integrating-thymeleaf-with-spring
//    TODO: to implement POST compensation-claims/create - dynamic field - addRow.
//    @RequestMapping(value="compensation-claims/create", params={"addRow"})
//    public String addRow(final CompensationClaim compensationClaim, final BindingResult bindingResult) {
//        compensationClaim.getRows().add(new Row());
//        return "seedstartermng";
//    }

//    ref: https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html#integrating-thymeleaf-with-spring
//    TODO: to implement POST compensation-claims/create - dynamic field - removeRow.
//    @RequestMapping(value="/seedstartermng", params={"removeRow"})
//    public String removeRow(
//            final SeedStarter seedStarter, final BindingResult bindingResult,
//            final HttpServletRequest req) {
//        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));
//        seedStarter.getRows().remove(rowId.intValue());
//        return "seedstartermng";
//    }

//    TODO: to implement POST compensation-claims/submit.
//    @ModelAttribute
//    @PostMapping("compensation-claims/submit")
//    // ref: check logged in user: https://stackoverflow.com/questions/45733193/how-to-get-id-of-currently-logged-in-user-using-spring-security-and-thymeleaf
//    // TODO: refactor using Sessions after it is implemented
//    public String submitCompensationClaims(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
//        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
//        Employee currentEmployee = currentUser.getEmployee();
//        return "redirect:/compensation-claims/view";
//    }
}
