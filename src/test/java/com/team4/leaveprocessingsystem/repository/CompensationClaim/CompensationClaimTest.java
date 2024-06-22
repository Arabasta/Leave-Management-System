package com.team4.leaveprocessingsystem.repository.CompensationClaim;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CompensationClaimTest {

    @Test
    public void testGetId() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getId()).isNull();
    }

    @Test
    public void testSetId() {
        CompensationClaim c = new CompensationClaim();
        c.setId(1);
        assertThat(c.getId()).isEqualTo(1);
    }

    // Test new CompensationClaim object should have null compensationClaimStatus
    @Test
    public void testGetCompensationClaimStatus() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getClaimStatus()).isNull();
    }

    // Test enums in case they were updated and misspelled with lowercase
    @Test
    public void testSetCompensationClaimStatus() {
        CompensationClaim c = new CompensationClaim();
        for (CompensationClaimStatusEnum status : CompensationClaimStatusEnum.values()) {
            c.setClaimStatus(status);
            assertThat(c.getClaimStatus().name().toUpperCase())
                    .isEqualTo(status.toString());
        }
    }

    @Test
    public void testGetClaimingEmployee() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getClaimingEmployee()).isNull();
    }

    @Test
    public void testSetClaimingEmployee() {
        CompensationClaim c = new CompensationClaim();
        Employee e = new Employee();
        c.setClaimingEmployee(e);
        assertThat(c.getClaimingEmployee()).isEqualTo(e);
    }

    @Test
    public void testGetClaimedDateTime() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getClaimDateTime()).isNull();
    }

    @Test
    public void testSetClaimedDateTime() {
        CompensationClaim c = new CompensationClaim();
        LocalDateTime localDateTime = LocalDateTime.now();
        c.setClaimDateTime(localDateTime);
        assertThat(c.getClaimDateTime()).isEqualTo(localDateTime);
    }

    @Test
    public void testGetOvertimeStartDateTime() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getOvertimeStart()).isNull();
    }

    @Test
    public void testSetOvertimeStartDateTime() {
        CompensationClaim c = new CompensationClaim();
        LocalDateTime localDateTime = LocalDateTime.now();
        c.setOvertimeStart(localDateTime);
        assertThat(c.getOvertimeStart()).isEqualTo(localDateTime);
    }

    @Test
    public void testGetOvertimeEndDateTime() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getOvertimeEnd()).isNull();
    }

    @Test
    public void testSetOvertimeEndDateTime() {
        CompensationClaim c = new CompensationClaim();
        LocalDateTime localDateTime = LocalDateTime.now();
        c.setOvertimeEnd(localDateTime);
        assertThat(c.getOvertimeEnd()).isEqualTo(localDateTime);
    }

    @Test
    public void testGetOvertimeHours() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getOvertimeHours()).isEqualTo(0f);
    }

    @Test
    public void testSetOvertimeHours() {
        CompensationClaim c = new CompensationClaim();
        c.setOvertimeHours(1f);
        assertThat(c.getOvertimeHours()).isEqualTo(1f);
    }

    @Test
    public void testGetCompensationLeaveRequested() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getCompensationLeaveRequested()).isEqualTo(0f);
    }

    @Test
    public void testSetCompensationLeaveRequested() {
        CompensationClaim c = new CompensationClaim();
        c.setCompensationLeaveRequested(1f);
        assertThat(c.getCompensationLeaveRequested()).isEqualTo(1f);
    }

    @Test
    public void testGetApprovingManager() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getApprovingManager()).isNull();
    }

    @Test
    public void testSetApprovingManager() {
        CompensationClaim c = new CompensationClaim();
        Manager m = new Manager();
        c.setApprovingManager(m);
        assertThat(c.getApprovingManager()).isEqualTo(m);
    }

    @Test
    public void testReviewedDateTime() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getReviewedDateTime()).isNull();
    }

    @Test
    public void testSetReviewedDateTime() {
        CompensationClaim c = new CompensationClaim();
        LocalDateTime localDateTime = LocalDateTime.now();
        c.setReviewedDateTime(localDateTime);
        assertThat(c.getReviewedDateTime()).isEqualTo(localDateTime);
    }

    @Test
    public void testGetComments() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getComments()).isNull();
    }

    @Test
    public void testSetComments() {
        CompensationClaim c = new CompensationClaim();
        c.setComments("testSetComments");
        assertThat(c.getComments()).isEqualTo("testSetComments");
    }
}
