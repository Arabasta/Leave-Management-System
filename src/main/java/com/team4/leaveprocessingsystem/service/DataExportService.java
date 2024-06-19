package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.interfacemethods.IDataExport;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.util.StringCleaningUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataExportService implements IDataExport {

    @Override
    public void downloadManagerReportingCompensationClaimsCSV(PrintWriter writer, List<CompensationClaim> list) {
        writer.write("Id,Claim Status,Claiming Employee,OvertimeStart,OvertimeEnd,OvertimeHours," +
                "CompensationLeaveRequested,ClaimDateTime,ApprovingManager,ReviewedDateTime,Comments\n");
        for(CompensationClaim claim: list) {
            writer.write(claim.getId()
                    + "," + claim.getClaimStatus().name()
                    + "," + claim.getClaimingEmployee().getName()
                    + "," + claim.getOvertimeStart().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm"))
                    + "," + claim.getOvertimeEnd().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm"))
                    + "," + claim.getOvertimeHours()
                    + "," + claim.getCompensationLeaveRequested()
                    + "," + claim.getClaimDateTime().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm"))
                    + "," + claim.getApprovingManager().getName()
                    + "," + (((claim.getReviewedDateTime() != null) ? // date != null ? date : null
                    claim.getReviewedDateTime().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm")) : null))
                    + "," + StringCleaningUtil.forCSV(claim.getComments()) + "\n"
            );
        }
        writer.close();
    }

    @Override
    public void downloadManagerReportingLeaveApplicationsCSV(PrintWriter writer, ArrayList<LeaveApplication> applications) {
        writer.write("Id,SubmittingEmployee,ReviewingManager,LeaveStatus,LeaveType,StartDate,EndDate" +
                "SubmissionReason,RejectionReason,WorkDissemination,ContactDetails\n");
        for(LeaveApplication application: applications) {
            writer.write(application.getId()
                    + "," + application.getSubmittingEmployee().getName()
                    + "," + application.getReviewingManager().getName()
                    + "," + application.getLeaveStatus().name()
                    + "," + application.getLeaveType().name()
                    + "," + application.getStartDate().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"))
                    + "," + application.getEndDate().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"))
                    + "," + StringCleaningUtil.forCSV(application.getSubmissionReason())
                    + "," + StringCleaningUtil.forCSV(application.getRejectionReason())
                    + "," + StringCleaningUtil.forCSV(application.getWorkDissemination())
                    + "," + StringCleaningUtil.forCSV(application.getContactDetails()) + "\n"
            );
        }
        writer.close();
    }
}