package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {
    @Autowired
    ManagerRepository managerRepository;

    @Transactional
    public Manager findManagerById(Integer id) {
        return managerRepository.findById(id).orElseThrow();
    }

}