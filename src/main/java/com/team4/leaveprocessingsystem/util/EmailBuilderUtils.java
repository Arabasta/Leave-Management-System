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
        String applicationUrl = "/leave/managerView/" + leaveApplication.getId();

        LeaveStatusEnum leaveStatus = leaveApplication.getLeaveStatus();
        if (leaveStatus == LeaveStatusEnum.APPLIED){
            subject = "New Leave Application: " + employeeName +
                    " - Duration: " + startDate + " to " + endDate;
        }
        else if (leaveStatus == LeaveStatusEnum.UPDATED){
            subject = "Updated Leave Application: " + employeeName +
                    " - Duration: " + startDate + " to " + endDate;
        }

        String text = "Dear " + managerName +",\n" +
                "\n" +
                "You have a new leave application pending your approval.\n" +
                "\n" +
                "Employee Name: " + employeeName +"\n" +
                "\n" +
                "Leave Details:\n" +
                "- Start Date: " + startDate + "\n" +
                "- End Date: " + endDate + "\n" +
                "- Leave Type: " + leaveType + "\n" +
                "- Reason: " + reason + "\n" +
                "\n" +
                "To review the leave application:" + "\n" +
                "Link: " + applicationUrl;

        emailBuilder.put("recipient", managerEmail);
        emailBuilder.put("subject", subject);
        emailBuilder.put("text", text);

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
        String applicationUrl = "/leave/view/" + leaveApplication.getId();

        LeaveStatusEnum leaveStatus = leaveApplication.getLeaveStatus();
        if (leaveStatus == LeaveStatusEnum.APPROVED){
            subject = "Leave Application approved: " + employeeName +
                    " - Duration: " + startDate + " to " + endDate;
        }
        else if (leaveStatus == LeaveStatusEnum.REJECTED){
            subject = "Leave Application rejected: " + employeeName +
                    " - Duration: " + startDate + " to " + endDate;
        }

        String text = "Dear " + employeeName +",\n" +
                "\n" +
                "Your leave application has been " + leaveStatus + ".\n" +
                "\n" +
                "Employee Name: " + employeeName +"\n" +
                "\n" +
                "Leave Details:\n" +
                "- Start Date: " + startDate + "\n" +
                "- End Date: " + endDate + "\n" +
                "- Leave Type: " + leaveType + "\n" +
                "- Reason: " + reason + "\n" +
                "\n" +
                "Manager Name: " + managerName +"\n" +
                "To view your leave application:" + "\n" +
                "Link: " + applicationUrl;

        emailBuilder.put("recipient", employeeEmail);
        emailBuilder.put("subject", subject);
        emailBuilder.put("text", text);

        return emailBuilder;
    }
}
