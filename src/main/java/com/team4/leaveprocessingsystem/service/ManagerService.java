package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.interfacemethods.IManager;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.LeaveApplicationRepository;
import com.team4.leaveprocessingsystem.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerService implements IManager {
    private final ManagerRepository managerRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository, LeaveApplicationRepository leaveApplicationRepository) {
        this.managerRepository = managerRepository;
        this.leaveApplicationRepository = leaveApplicationRepository;
    }
    @Transactional
    public Manager findManagerById(Integer id) {
        return managerRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<Manager> findAllManagers() {
        return managerRepository.findAll();
    }

    @Transactional
    public List<Employee> findManagersByName(String name) {
        return managerRepository.findManagerByName(name);
    }

    @Transactional
    public Map<Employee, LeaveApplication> findSubordinatesOnLeaveDuring(Manager manager, LocalDate start, LocalDate end) {
        List<LeaveApplication> leaveApplications = leaveApplicationRepository
                .findSubordinatesLeaveApplicationsByReviewingManager_Id(manager.getId())
                .stream().filter(la -> !la.getStartDate().isBefore(start) && !la.getEndDate().isAfter(end))
                .toList();
        Map<Employee, LeaveApplication> map = new HashMap<>();
        for (LeaveApplication la : leaveApplications) {
            map.put(la.getSubmittingEmployee(), la);
        }
        return map;
    }
}
