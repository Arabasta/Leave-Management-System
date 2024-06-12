package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IUser;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class UserService implements UserDetailsService, IUser {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // note: self invocation problem
        return findByUsername(username);
    }

    @Override
    @Transactional
    public boolean save(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException(user.getUsername(), e);
        }
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User " + username + " not found"));
    }

    @Override
    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
    }

    @Override
    @Transactional
    public long count() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public List<User> findUserRolesByEmployeeId(Integer employeeId) {
        return userRepository.findUserRolesByEmployeeId(employeeId);
    }
}
