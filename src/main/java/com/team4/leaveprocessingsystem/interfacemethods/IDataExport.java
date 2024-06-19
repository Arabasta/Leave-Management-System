package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.LeaveApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public interface IDataExport {
    void downloadManagerReportingCompensationClaimsCSV(PrintWriter printWriter, List<CompensationClaim> list) throws IOException;

    void downloadManagerReportingLeaveApplicationsCSV(PrintWriter writer, ArrayList<LeaveApplication> applications) throws IOException;
}