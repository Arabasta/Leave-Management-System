package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;

import java.util.List;

public interface IUser {
    boolean save(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    long count();

    List<User> findUserRolesByEmployeeId(Integer employeeId);
}
