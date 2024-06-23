package com.team4.leaveprocessingsystem.repository.JobDesignation;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JobDesignationTest {
    @Test
    public void testGetId() {
        JobDesignation jd = new JobDesignation();
        assertThat(jd.getId()).isNull();
    }

    @Test
    public void testSetId() {
        JobDesignation jd = new JobDesignation();
        jd.setId(1);
        assertThat(jd.getId()).isEqualTo(1);
    }

    @Test
    public void testGetName() {
        JobDesignation jd = new JobDesignation();
        assertThat(jd.getName()).isNull();
    }

    @Test
    public void testSetName() {
        JobDesignation jd = new JobDesignation();
        jd.setName("test");
        assertThat(jd.getName()).isEqualTo("test");
    }

    @Test
    public void testGetDefaultAnnualLeaves() {
        JobDesignation jd = new JobDesignation(null, 15);
        assertThat(jd.getDefaultAnnualLeaves()).isEqualTo(15);
    }

    @Test
    public void testSetDefaultAnnualLeaves() {
        JobDesignation jd = new JobDesignation();
        jd.setDefaultAnnualLeaves(15);
        assertThat(jd.getDefaultAnnualLeaves()).isEqualTo(15);
    }
}
