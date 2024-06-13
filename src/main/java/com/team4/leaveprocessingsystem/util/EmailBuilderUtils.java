package com.team4.leaveprocessingsystem.util;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EmailBuilderUtils {
    public static Map<String, String> buildNotificationEmail(LeaveApplication leaveApplication){
        Map<String, String> emailBuilder = new HashMap<>();

        Employee employee = leaveApplication.getSubmittingEmployee();

        String employeeName = employee.getName();
        String managerName = employee.getManager().getName();
        String employeeEmail= employee.getUsers().get(0).getEmail();
        String subject = "";
        String startDate = leaveApplication.getStartDate().toString();
        String endDate = leaveApplication.getEndDate().toString();
        String leaveType = leaveApplication.getLeaveType().toString();
        String reason= leaveApplication.getSubmissionReason();
        String rejectionReason= leaveApplication.getRejectionReason();
        String rejectionReasonStr = "";
        if (rejectionReason != null && rejectionReason != ""){
            rejectionReasonStr = "- Rejection Reason: " + rejectionReason + "<br>";
        }
        String applicationUrlForEmployee = "http://localhost:8080/leave/view/" + leaveApplication.getId();
        // TODO: Change the url to the manager's view once implemented
        String applicationUrlForManager = "http://localhost:8080/leave/view/" + leaveApplication.getId();

        String leaveStatus = leaveApplication.getLeaveStatus().toString();

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("${managerName}", managerName);
        placeholders.put("${employeeName}", employeeName);
        placeholders.put("${startDate}", startDate);
        placeholders.put("${endDate}", endDate);
        placeholders.put("${leaveType}", leaveType);
        placeholders.put("${reason}", reason);

        placeholders.put("${leaveStatus}", leaveStatus);
        placeholders.put("${rejectionReasonStr}", rejectionReasonStr);

        Path path;

        if (leaveStatus.equals("APPLIED") || leaveStatus.equals("UPDATED")){
            path = Path.of("src/main/resources/templates/leaveApplication/emailToManagerTemplate.html");
            placeholders.put("${applicationUrl}", applicationUrlForManager);
            if (leaveStatus.equals("APPLIED")){
                subject = "New Leave Application: " + employeeName +
                        " - Duration: " + startDate + " to " + endDate;
            }
            else{
                subject = "Updated Leave Application: " + employeeName +
                        " - Duration: " + startDate + " to " + endDate;
            }
        }
        else{
            path = Path.of("src/main/resources/templates/leaveApplication/emailToEmployeeTemplate.html");
            placeholders.put("${applicationUrl}", applicationUrlForEmployee);
            subject = "Leave Application " + leaveStatus + ": " + employeeName +
                    " - Duration: " + startDate + " to " + endDate;
        }

        String htmlContent = "";
        try{
            htmlContent = Files.readString(path, StandardCharsets.UTF_8);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            htmlContent = htmlContent.replace(entry.getKey(), entry.getValue());
        }

        String escapedHtmlContent = htmlContent.replaceAll("\\r?\\n", "\\\\n");
        escapedHtmlContent = escapedHtmlContent.replace("\"", "\\\"");

        emailBuilder.put("recipient", employeeEmail);
        emailBuilder.put("subject", subject);
        emailBuilder.put("text", escapedHtmlContent);

        return emailBuilder;
    }
}
