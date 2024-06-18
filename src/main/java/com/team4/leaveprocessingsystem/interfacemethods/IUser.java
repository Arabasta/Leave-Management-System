package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUser {
    boolean save(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    long count();

    List<User> findUserRolesByEmployeeId(Integer employeeId);

    List<User> findByEmployeeId(Integer employeeId);

    public User findById(int id);
    public List<User> findUsersByUsername(String keyword);
    List<User> findUsersByEmail(String email);

    List<User> findUsersById(String id);
    List<User> findUsersByRoleType(String keyword);
}
