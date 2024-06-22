package com.team4.leaveprocessingsystem.Employee;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class EmployeeTest {
    @Test
    public void testGetId() {
        Employee e = new Employee();
        assertThat(e.getId()).isNull();
    }

    @Test
    public void testSetId() {
        Employee e = new Employee();
        e.setId(1);
        assertThat(e.getId()).isEqualTo(1);
    }

    @Test
    public void testGetJobDesignation() {
        Employee e = new Employee();
        assertThat(e.getJobDesignation()).isNull();
    }

    @Test
    public void testSetJobDesignation() {
        Employee e = new Employee();
        JobDesignation jd = new JobDesignation("test", 1);
        assertThat(e.getJobDesignation()).isEqualTo(jd);
        assertThat(e.getJobDesignation().getDefaultAnnualLeaves()).isEqualTo(1);
    }

    @Test
    public void testGetManager() {
        Employee e = new Employee();
        assertThat(e.getManager()).isNull();
    }

    @Test
    public void testSetManager() {
        Employee e = new Employee();
        Manager m = new Manager();
        assertThat(e.getManager()).isEqualTo(m);
    }

    @Test
    public void testGetLeaveBalance() {
        Employee e = new Employee();
        assertThat(e.getLeaveBalance()).isNull();
    }

    @Test
    public void testSetLeaveBalance() {
        Employee e = new Employee();
        LeaveBalance lb = new LeaveBalance();
        e.setLeaveBalance(lb);
        assertThat(e.getLeaveBalance()).isEqualTo(lb);
    }

    @Test
    public void testGetName() {
        Employee e = new Employee();
        assertThat(e.getName()).isNull();
    }

    @Test
    public void testSetName() {
        Employee e = new Employee();
        e.setName("test");
        assertThat(e.getName()).isEqualTo("test");
    }
}
