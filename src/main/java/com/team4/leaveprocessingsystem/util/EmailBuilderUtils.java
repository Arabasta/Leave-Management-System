package com.team4.leaveprocessingsystem.util;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EmailBuilderUtils {
    // used for notifying manager when an employee applies/updates a leave application
    public static Map<String, String> buildLeaveApplicationEmail(LeaveApplication leaveApplication){
        Map<String, String> emailBuilder = new HashMap<>();

        Employee employee = leaveApplication.getSubmittingEmployee();

        String employeeName = employee.getName();
        String managerName = employee.getManager().getName();
        String managerEmail= employee.getManager().getUsers().get(0).getEmail();
        String subject = "";
        LocalDate startDate = leaveApplication.getStartDate();
        LocalDate endDate = leaveApplication.getEndDate();
        String leaveType = leaveApplication.getLeaveType().toString();
        String reason= leaveApplication.getSubmissionReason();
        String applicationUrl = "http://localhost:8080/leave/managerView/" + leaveApplication.getId();

        LeaveStatusEnum leaveStatus = leaveApplication.getLeaveStatus();
        if (leaveStatus == LeaveStatusEnum.APPLIED){
            subject = "New Leave Application: " + employeeName +
                    " - Duration: " + startDate + " to " + endDate;
        }
        else if (leaveStatus == LeaveStatusEnum.UPDATED){
            subject = "Updated Leave Application: " + employeeName +
                    " - Duration: " + startDate + " to " + endDate;
        }

        String text = "Dear " + managerName + ",<br>" +
                "<br>" +
                "You have a new leave application pending your approval.<br>" +
                "<br>" +
                "Employee Name: " + employeeName + "<br>" +
                "<br>" +
                "Leave Details:<br>" +
                "- Start Date: " + startDate + "<br>" +
                "- End Date: " + endDate + "<br>" +
                "- Leave Type: " + leaveType + "<br>" +
                "- Reason: " + reason + "<br>" +
                "<br>" +
                "To review the leave application:<br>" +
                "<a href=\"" + applicationUrl + "\">View Leave Application</a>";

        String escapedText = text.replace("\"", "\\\"").replace("\n", "\\n");

        emailBuilder.put("recipient", managerEmail);
        emailBuilder.put("subject", subject);
        emailBuilder.put("text", escapedText);

        return emailBuilder;
    }

    // used for notifying employee when a manager approves/rejects a leave application
    public static Map<String, String> buildLeaveApplicationResultEmail(LeaveApplication leaveApplication){
        Map<String, String> emailBuilder = new HashMap<>();

        Employee employee = leaveApplication.getSubmittingEmployee();

        String employeeName = employee.getName();
        String managerName = employee.getManager().getName();
        String employeeEmail= employee.getUsers().get(0).getEmail();
        String subject = "";
        LocalDate startDate = leaveApplication.getStartDate();
        LocalDate endDate = leaveApplication.getEndDate();
        String leaveType = leaveApplication.getLeaveType().toString();
        String reason= leaveApplication.getSubmissionReason();
        String rejectionReason= leaveApplication.getRejectionReason();
        String rejectionReasonStr = "";
        if (rejectionReason != null && rejectionReason != ""){
            rejectionReasonStr = "- Rejection Reason: " + rejectionReason + "<br>";
        }
        String applicationUrl = "http://localhost:8080/leave/view/" + leaveApplication.getId();

        String leaveStatus = leaveApplication.getLeaveStatus().toString();
        subject = "Leave Application " + leaveStatus + ": " + employeeName +
                " - Duration: " + startDate + " to " + endDate;

        String text = "Dear " + employeeName + ",<br>" +
                "<br>" +
                "Your leave application has been " + leaveStatus + ".<br>" +
                "<br>" +
                "Employee Name: " + employeeName + "<br>" +
                "Manager Name: " + managerName + "<br>" +
                "<br>" +
                "Leave Details:<br>" +
                "- Start Date: " + startDate + "<br>" +
                "- End Date: " + endDate + "<br>" +
                "- Leave Type: " + leaveType + "<br>" +
                "- Reason: " + reason + "<br>" +
                rejectionReasonStr +
                "<br>" +
                "To view your leave application:<br>" +
                "<a href=\"" + applicationUrl + "\">View Leave Application</a>";

        String escapedText = text.replace("\"", "\\\"").replace("\n", "\\n");

        emailBuilder.put("recipient", employeeEmail);
        emailBuilder.put("subject", subject);
        emailBuilder.put("text", escapedText);

        return emailBuilder;
    }
}
