package com.team4.leaveprocessingsystem.model.dataTransferObjects;

import com.team4.leaveprocessingsystem.model.Employee;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DataExportDTO {
    private ArrayList<Employee> employees;

    public DataExportDTO(List<Employee> employees) {
        this.employees = (ArrayList<Employee>) employees;
    }
}
