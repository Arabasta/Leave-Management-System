package com.team4.leaveprocessingsystem.service.schedulers;

import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

// reference: https://reflectoring.io/spring-scheduler/
@Service
public class LeaveResetScheduleService {

    private final LeaveBalanceService leaveBalanceService;

    public LeaveResetScheduleService(LeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @Scheduled(cron = "0 0 0 1 1 *") // Runs at midnight 1st Jan (ss mm hh dd mm day)
    public void resetLeaves() {
        leaveBalanceService.resetLeaves();
    }
}
