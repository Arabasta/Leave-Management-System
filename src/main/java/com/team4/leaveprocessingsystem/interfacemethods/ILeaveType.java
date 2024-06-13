package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.LeaveType;

import java.util.List;

public interface ILeaveType {

    long count();

    LeaveType createLeaveType(LeaveType leaveType);

    LeaveType updateLeaveType(LeaveType leaveType);

    List<LeaveType> findAllLeaveTypes();

    void removeLeaveType(LeaveType leaveType);


    LeaveType findLeaveTypeById(Integer id);


    /*
    public LeaveType findLeaveTypeById(Integer id);
    public List<String> getTypeNames();
    public LeaveType findByType(String type);

    boolean save(LeaveBalance leaveBalance);
    void update(LeaveApplication leaveApplication);
    LeaveBalance findByEmployee(int employee_id);

     */
}
