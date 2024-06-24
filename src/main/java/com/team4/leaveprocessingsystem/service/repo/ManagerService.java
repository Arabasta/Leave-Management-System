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

    @Override
    @Transactional
    public List<Manager> findAllExcept(Integer id) { return managerRepository.findAllExcept(id);}
}
