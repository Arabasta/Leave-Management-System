package com.team4.leaveprocessingsystem.User;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    @Test
    public void testGetId() {
        User u = new User();
        assertThat(u.getId()).isNull();
    }

    @Test
    public void testSetId() {
        User u = new User();
        u.setId(1);
        assertThat(u.getId()).isEqualTo(1);
    }

    @Test
    public void testGetEmployee() {
        User u = new User();
        assertThat(u.getEmployee()).isNull();
    }

    @Test
    public void testSetEmployee() {
        User u = new User();
        Employee e = new Employee();
        u.setEmployee(e);
        assertThat(u.getEmployee()).isEqualTo(e);
    }

    @Test
    public void testGetRole() {
        User u = new User();
        assertThat(u.getRole()).isNull();
    }

    @Test
    public void testSetRole() {
        User u = new User();
        for (RoleEnum role : RoleEnum.values()) {
            u.setRole(role);
            assertThat(u.getRole()).isEqualTo(role);
        }
    }

    @Test
    public void testGetUsername() {
        User u = new User();
        assertThat(u.getUsername()).isNull();
    }

    @Test
    public void testSetUsername() {
        User u = new User();
        u.setUsername("test");
        assertThat(u.getUsername()).isEqualTo("test");
    }

    @Test
    public void testGetPassword() {
        User u = new User();
        assertThat(u.getPassword()).isNull();
    }

    @Test
    public void testSetPassword() {
        User u = new User();
        u.setPassword("test");
        assertThat(u.getPassword()).isEqualTo("test");
    }

    @Test
    public void testGetEmail() {
        User u = new User();
        assertThat(u.getEmail()).isNull();
    }

    @Test
    public void testSetEmail() {
        User u = new User();
        u.setEmail("test");
        assertThat(u.getEmail()).isEqualTo("test");
    }

}
