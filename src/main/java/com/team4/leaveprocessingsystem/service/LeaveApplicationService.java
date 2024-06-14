package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationNotFoundException;
import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.LeaveApplicationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LeaveApplicationService implements ILeaveApplication {
    private final LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    @Override
    @Transactional
    public boolean save(LeaveApplication leaveApplication) {
        try {
            leaveApplicationRepository.save(leaveApplication);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException("leave application from: " +
                    leaveApplication.getSubmittingEmployee().getName(), e);
        }
    }

    @Override
    @Transactional
    public LeaveApplication findLeaveApplicationById(Integer id){
        return leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new LeaveApplicationNotFoundException("Leave Application Not Found"));
    }

    @Override
    @Transactional
    public LeaveApplication getLeaveApplicationIfBelongsToEmployee(int id, Employee employee) {
        LeaveApplication leaveApplication = findLeaveApplicationById(id);
        // Ensure an employee only accesses his own leave applications
        if (!leaveApplication.getSubmittingEmployee().getId().equals(employee.getId())) {
            throw new LeaveApplicationNotFoundException("Leave Application Not Found");
        }
        return leaveApplication;
    }

    @Override
    public Map<String, List<LeaveApplication>> findLeaveApplicationsPendingApprovalByManager(Manager manager) {
        return null;
    }

    @Override
    @Transactional
    public long count() {
        return leaveApplicationRepository.count();
    }

    @Override
    @Transactional
    public List<LeaveApplication> findSubordinatesLeaveApplicationsByReviewingManager_Id(int managerId){
        return leaveApplicationRepository.findSubordinatesLeaveApplicationsByReviewingManager_Id(managerId);
    }

    @Override
    @Transactional
    public List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee) {
        return leaveApplicationRepository.findBySubmittingEmployee(submittingEmployee);
    }

    @Override
    public List<LeaveApplication> findByEmployeeName(String name) {
        return leaveApplicationRepository.findByName(name);
    }

    @Override
    public List<LeaveApplication> findByEmployeeId(int id) {
        return leaveApplicationRepository.findBySubmittingEmployeeId(id);
    }

    @Override
    @Transactional
    public List<LeaveApplication> getLeaveApplicationIfBelongsToManagerSubordinates(List<LeaveApplication> applications, int managerId) {
        List<LeaveApplication> applicationsBelongToManagerSubordinates = new ArrayList<>();
        for (LeaveApplication application : applications) {
            if (application.getReviewingManager().getId() == managerId) {
                applicationsBelongToManagerSubordinates.add(application);
            }
        }
        return applicationsBelongToManagerSubordinates;
    }

}
