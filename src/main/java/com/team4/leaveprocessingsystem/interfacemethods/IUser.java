package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.User;

public interface IUser {
    boolean save(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    long count();
}
