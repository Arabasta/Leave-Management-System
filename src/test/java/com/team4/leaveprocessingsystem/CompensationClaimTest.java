package com.team4.leaveprocessingsystem;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.service.CompensationClaimService;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

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
        assertThat(c.getCompensationClaimStatus()).isNull();
    }

    // Test enums in case they were updated and misspelled with lowercase
    @Test
    public void testSetCompensationClaimStatus() {
        CompensationClaim c = new CompensationClaim();
        for (CompensationClaimStatusEnum status : CompensationClaimStatusEnum.values()) {
            c.setCompensationClaimStatus(status);
            assertThat(c.getCompensationClaimStatus().name().toUpperCase())
                    .isEqualTo(status.toString());
        }
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
    public void testGetOvertimeStartDateTime() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getOvertimeStartDateTime()).isNull();
    }

    @Test
    public void testSetOvertimeStartDateTime() {
        CompensationClaim c = new CompensationClaim();
        LocalDateTime localDateTime = LocalDateTime.now();
        c.setOvertimeStartDateTime(localDateTime);
        assertThat(c.getOvertimeStartDateTime()).isEqualTo(localDateTime);
    }

    @Test
    public void testGetOvertimeEndDateTime() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getOvertimeEndDateTime()).isNull();
    }

    @Test
    public void testSetOvertimeEndDateTime() {
        CompensationClaim c = new CompensationClaim();
        LocalDateTime localDateTime = LocalDateTime.now();
        c.setOvertimeEndDateTime(localDateTime);
        assertThat(c.getOvertimeEndDateTime()).isEqualTo(localDateTime);
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

    //    TODO: verify if test for Manager is needed;

    @Test
    public void testGetClaimedDateTime() {
        CompensationClaim c = new CompensationClaim();
        assertThat(c.getClaimedDateTime()).isNull();
    }

    @Test
    public void testSetClaimedDateTime() {
        CompensationClaim c = new CompensationClaim();
        LocalDateTime localDateTime = LocalDateTime.now();
        c.setClaimedDateTime(localDateTime);
        assertThat(c.getClaimedDateTime()).isEqualTo(localDateTime);
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

    //    TODO: verify if test for Employee is needed;

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
