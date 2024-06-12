package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.interfacemethods.IManager;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerService implements IManager {
    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }
    @Transactional
    public Manager findManagerById(Integer id) {
        return managerRepository.findById(id).orElseThrow();
    }

    @Transactional
    public List<Manager> findAllManagers() {
        return managerRepository.findAll();
    }

}
