package com.team4.leaveprocessingsystem.repository.Manager;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.Manager;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ManagerTest {
    @Test
    public void testGetId() {
        Manager m = new Manager();
        assertThat(m.getId()).isNull();
    }

    @Test
    public void testSetId() {
        Manager m = new Manager();
        m.setId(1);
        assertThat(m.getId()).isEqualTo(1);
    }

    @Test
    public void testGetName() {
        Manager m = new Manager();
        assertThat(m.getName()).isNull();
    }

    @Test
    public void testSetName() {
        Manager m = new Manager();
        m.setName("test");
        assertThat(m.getName()).isEqualTo("test");
    }

    @Test
    public void testGetJobDesignation() {
        Manager m = new Manager();
        assertThat(m.getJobDesignation()).isNull();
    }

    @Test
    public void testSetJobDesignation() {
        Manager m = new Manager();
        JobDesignation jd = new JobDesignation("test", 1);
        m.setJobDesignation(jd);
        assertThat(m.getJobDesignation()).isEqualTo(jd);
        assertThat(m.getJobDesignation().getDefaultAnnualLeaves()).isEqualTo(1);
    }

    @Test
    public void testGetManager() {
        Manager m = new Manager();
        assertThat(m.getManager()).isNull();
    }

    @Test
    public void testSetManager() {
        Manager m1 = new Manager();
        Manager m2 = new Manager();
        m1.setManager(m2);
        assertThat(m1.getManager()).isEqualTo(m2);
    }

    @Test
    public void testGetLeaveBalance() {
        Manager m = new Manager();
        assertThat(m.getLeaveBalance()).isNull();
    }

    @Test
    public void testSetLeaveBalance() {
        Manager m = new Manager();
        LeaveBalance lb = new LeaveBalance();
        m.setLeaveBalance(lb);
        assertThat(m.getLeaveBalance()).isEqualTo(lb);
    }


}
