package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveType;
import com.team4.leaveprocessingsystem.repository.JobDesignationRepository;
import com.team4.leaveprocessingsystem.repository.LeaveTypeRepository;
import com.team4.leaveprocessingsystem.service.LeaveTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobDesignationSeeder {

    private final JobDesignationRepository jobDesignationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeService leaveTypeService;

    public JobDesignationSeeder(JobDesignationRepository jobDesignationRepository,
                                LeaveTypeRepository leaveTypeRepository, LeaveTypeService leaveTypeService) {
        this.jobDesignationRepository = jobDesignationRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveTypeService = leaveTypeService;
    }

    public void seed() {
        if (jobDesignationRepository.count() == 0) {

            // Create Leave Type List for Job Designation initialization
            LeaveType annualLeave = leaveTypeService.findLeaveTypeById(1);
            LeaveType medicalLeave = leaveTypeService.findLeaveTypeById(2);
            LeaveType compensationLeave = leaveTypeService.findLeaveTypeById(3);
            LeaveType compassionateLeave = leaveTypeService.findLeaveTypeById(4);
            LeaveType unpaidLeave = leaveTypeService.findLeaveTypeById(5);

            // ************************ ADMINISTRATIVE **********************************
            List<LeaveType> leaveTypesForAdmininstrative = new ArrayList<LeaveType>();
            leaveTypesForAdmininstrative.add(annualLeave);
            leaveTypesForAdmininstrative.add(medicalLeave);
            leaveTypesForAdmininstrative.add(compassionateLeave);
            leaveTypesForAdmininstrative.add(unpaidLeave);

            JobDesignation administrative = new JobDesignation("administrative",
                    14,
                    leaveTypesForAdmininstrative);

            jobDesignationRepository.save(administrative);

            // ************************ MANAGEMENT **********************************
            List<LeaveType> leaveTypesForManagement = new ArrayList<LeaveType>();
            leaveTypesForManagement.add(annualLeave);
            leaveTypesForManagement.add(medicalLeave);
            leaveTypesForManagement.add(compassionateLeave);
            leaveTypesForManagement.add(unpaidLeave);

            JobDesignation management = new JobDesignation("management", 30, leaveTypesForManagement);

            jobDesignationRepository.save(management);

            // ************************ CLEANING **********************************
            List<LeaveType> leaveTypesForCleaning = new ArrayList<LeaveType>();
            leaveTypesForCleaning.add(annualLeave);
            leaveTypesForCleaning.add(medicalLeave);
            leaveTypesForCleaning.add(compassionateLeave);

            JobDesignation cleaning = new JobDesignation("cleaning", 3, leaveTypesForCleaning);
            jobDesignationRepository.save(cleaning);

            // ************************ INTERN **********************************
            List<LeaveType> leaveTypesForIntern = new ArrayList<LeaveType>();
            leaveTypesForCleaning.add(annualLeave);
            leaveTypesForCleaning.add(medicalLeave);
            leaveTypesForCleaning.add(compassionateLeave);
            leaveTypesForCleaning.add(compensationLeave);

            JobDesignation intern = new JobDesignation("intern", 10, leaveTypesForCleaning);
            jobDesignationRepository.save(intern);
        }
    }
}
