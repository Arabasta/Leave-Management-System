package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.interfacemethods.UserInterface;


@Service
public class UserService implements UserDetailsService, UserInterface {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("User " + username + " not found");

        return user;
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
