package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public interface IDataExport {
    void downloadViewEmployeesCompensationClaimsCSV(PrintWriter printWriter, List<CompensationClaim> list) throws IOException;
}