package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.interfacemethods.ILeaveType;
import com.team4.leaveprocessingsystem.model.LeaveType;
import com.team4.leaveprocessingsystem.repository.LeaveTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveTypeService implements ILeaveType {
    @Autowired
    LeaveTypeRepository leaveTypeRepository;
    @Autowired
    JobDesignationService jobDesignationService;

    public long count() {
        return leaveTypeRepository.count();
    }

    @Override
    @Transactional
    public List<LeaveType> findAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    @Override
    @Transactional
    public LeaveType findLeaveTypeById(Integer id) {
        return leaveTypeRepository.findById(id).orElse(null);
    }

    ;

    @Override
    @Transactional
    public LeaveType createLeaveType(LeaveType leaveType) {
        return leaveTypeRepository.save(leaveType);
    }

    @Override
    @Transactional
    public LeaveType updateLeaveType(LeaveType leaveType) {
        return leaveTypeRepository.save(leaveType);
    }

    @Override
    @Transactional
    public void removeLeaveType(LeaveType leaveType) {
        leaveTypeRepository.delete(leaveType);
    }


}
