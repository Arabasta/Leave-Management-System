package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.Exceptions.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IRole;
import com.team4.leaveprocessingsystem.model.Role;
import com.team4.leaveprocessingsystem.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IRole {
    @Autowired
    RoleRepository roleRepository;

    @Override
    @Transactional
    public boolean save(Role role) {
        try {
            roleRepository.save(role);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException(role.getName(), e);
        }
    }

    @Override
    @Transactional
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role " + name + " not found"));
    }
}
