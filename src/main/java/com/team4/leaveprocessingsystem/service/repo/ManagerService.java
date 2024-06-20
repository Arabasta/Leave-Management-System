package com.team4.leaveprocessingsystem.service.repo;

import com.team4.leaveprocessingsystem.interfacemethods.IManager;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.LeaveApplicationRepository;
import com.team4.leaveprocessingsystem.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerService implements IManager {
    private final ManagerRepository managerRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository, LeaveApplicationRepository leaveApplicationRepository) {
        this.managerRepository = managerRepository;
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    @Override
    @Transactional
    public Manager findManagerById(Integer id) {
        return managerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<Manager> findAllManagers() {
        return managerRepository.findAll();
    }

    @Override
    @Transactional
    public List<Employee> findManagersByName(String name) {
        return managerRepository.findManagerByName(name);
    }

    // TODO: Delete if not required. Also doesn't belong to this service
//    @Transactional
//    public Map<Employee, LeaveApplication> findSubordinatesOnLeaveDuring(Manager manager, LocalDate start, LocalDate end) {
//        List<LeaveApplication> leaveApplications = leaveApplicationRepository
//                .findSubordinatesLeaveApplicationsByReviewingManager_Id(manager.getId())
//                .stream().filter(la -> !la.getStartDate().isBefore(start) && !la.getEndDate().isAfter(end))
//                .toList();
//        Map<Employee, LeaveApplication> map = new HashMap<>();
//        for (LeaveApplication la : leaveApplications) {
//            map.put(la.getSubmittingEmployee(), la);
//        }
//        return map;
//    }
}
