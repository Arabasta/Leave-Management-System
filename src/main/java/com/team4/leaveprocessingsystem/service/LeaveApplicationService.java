package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.Exceptions.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.repository.LeaveApplicationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveApplicationService implements ILeaveApplication {
    @Autowired
    LeaveApplicationRepository leaveApplicationRepository;

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
        return leaveApplicationRepository.findById(id).get();
    }

    @Override
    @Transactional
    public long count() {
        return leaveApplicationRepository.count();
    }
}
