package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Role;

public interface IRole {
    boolean save(Role role);
    Role findByName(String name);
}
