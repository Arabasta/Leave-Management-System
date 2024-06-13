package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.LeaveType;
import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.repository.LeaveTypeRepository;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.LeaveTypeService;
import org.springframework.stereotype.Service;

@Service
public class LeaveTypeSeeder {
    private final LeaveTypeService leaveTypeService;
    private final JobDesignationService jobDesignationService;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeSeeder(LeaveTypeService leaveTypeService
            , JobDesignationService jobDesignationService
            , LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeService = leaveTypeService;
        this.jobDesignationService = jobDesignationService;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public void seed() {
        if (leaveTypeService.count() == 0) {

            // *********************** ANNUAL LEAVE ******************************
            LeaveType leaveTypeAnnual = new LeaveType();
            leaveTypeAnnual.setLeaveType(LeaveTypeEnum.ANNUAL);
            leaveTypeAnnual.setDescription("Employees who have completed " +
                    "three months of service are entitled to annual leave. ");

            // possible leave lengths: ALL (1,2,5,14,30)
            /* TESTING
            List<LeaveLengthEnum> eligibleLeaveLengthsForAnnualLeave = new ArrayList<LeaveLengthEnum>();
            eligibleLeaveLengthsForAnnualLeave.addAll(Arrays.asList(LeaveLengthEnum.values()));
            leaveTypeAnnual.setEntitlement(eligibleLeaveLengthsForAnnualLeave);
            */


            // eligible job designations: ALL
            /* TESTING
            List<JobDesignation> eligibleJDsForAnnualLeaveType = jobDesignationService.listAllJobDesignations();
            leaveTypeAnnual.setEligibleJobDesignationsForLeaveType(eligibleJDsForAnnualLeaveType);
             */
            leaveTypeRepository.save(leaveTypeAnnual);

            // *********************** MEDICAL LEAVE ******************************
            LeaveType leaveTypeMedical = new LeaveType();
            leaveTypeMedical.setLeaveType(LeaveTypeEnum.MEDICAL);
            leaveTypeMedical.setDescription("Employees are entitled to paid outpatient sick leave \n and " +
                    "paid hospitalisation leave.");

            // possible leave lengths: ALL (1,2,5,14,30)
            /* TESTING
            List<LeaveLengthEnum> eligibleLeaveLengthsForMedicalLeave = new ArrayList<LeaveLengthEnum>();
            eligibleLeaveLengthsForMedicalLeave.addAll(Arrays.asList(LeaveLengthEnum.values()));
            leaveTypeMedical.setEntitlement(eligibleLeaveLengthsForMedicalLeave);
            */

            // eligible job designations: ALL

            /* TESTING
            List<JobDesignation> eligibleJDsForMedicalLeaveType = jobDesignationService.listAllJobDesignations();
            leaveTypeMedical.setEligibleJobDesignationsForLeaveType(eligibleJDsForMedicalLeaveType);
             */
            leaveTypeRepository.save(leaveTypeMedical);

            // *********************** COMPENSATION LEAVE ******************************
            LeaveType leaveTypeCompensation = new LeaveType();
            leaveTypeCompensation.setLeaveType(LeaveTypeEnum.COMPENSATION);
            leaveTypeCompensation.setDescription("This leave is offered to compensate employees \n" +
                    "for the extra hours they have worked beyond their regular working hours.");

            // possible leave lengths: (1,2)
            /* TESTING
            List<LeaveLengthEnum> eligibleLeaveLengthsForCompensationLeave = new ArrayList<LeaveLengthEnum>();
            eligibleLeaveLengthsForCompensationLeave.add(LeaveLengthEnum.ONE_DAY_LEAVE);
            eligibleLeaveLengthsForCompensationLeave.add(LeaveLengthEnum.TWO_DAY_LEAVE);
            leaveTypeCompensation.setEntitlement(eligibleLeaveLengthsForCompensationLeave);
            */

            // eligible job designations: intern

            /* TESTING
            List<JobDesignation> eligibleJDsForCompensationLeaveType = new ArrayList<JobDesignation>();
            eligibleJDsForCompensationLeaveType.add(jobDesignationService.findByName("intern"));
            leaveTypeCompensation.setEligibleJobDesignationsForLeaveType(eligibleJDsForCompensationLeaveType);


             */
            leaveTypeRepository.save(leaveTypeCompensation);

            // *********************** COMPASSIONATE LEAVE ******************************
            LeaveType leaveTypeCompassionate = new LeaveType();
            leaveTypeCompassionate.setLeaveType(LeaveTypeEnum.COMPASSIONATE);
            leaveTypeCompassionate.setDescription("Compassionate leave is paid time off that allows you to go \n" +
                    "to or prepare for a funeral for family members.");

            // possible leave lengths: (1,2,5)
            /* TESTING
            List<LeaveLengthEnum> eligibleLeaveLengthsForCompassionateLeave = new ArrayList<LeaveLengthEnum>();
            eligibleLeaveLengthsForCompassionateLeave.add(LeaveLengthEnum.ONE_DAY_LEAVE);
            eligibleLeaveLengthsForCompassionateLeave.add(LeaveLengthEnum.TWO_DAY_LEAVE);
            eligibleLeaveLengthsForCompassionateLeave.add(LeaveLengthEnum.FIVE_DAY_LEAVE);
            leaveTypeCompassionate.setEntitlement(eligibleLeaveLengthsForCompassionateLeave);
             */

            // eligible job designations: ALL
            /* TESTING
            List<JobDesignation> eligibleJDsForCompassionateLeaveType = jobDesignationService.listAllJobDesignations();
            leaveTypeCompassionate.setEligibleJobDesignationsForLeaveType(eligibleJDsForCompassionateLeaveType);
             */
            leaveTypeRepository.save(leaveTypeCompassionate);

            // *********************** UNPAID LEAVE ******************************
            LeaveType leaveTypeUnpaid = new LeaveType();
            leaveTypeUnpaid.setLeaveType(LeaveTypeEnum.UNPAID);
            leaveTypeUnpaid.setDescription("It refers to approved leaves that employees take beyond their \n" +
                    "paid annual leave entitlements.");

            // possible leave lengths: (5,14,30)
            /* TESTING
            List<LeaveLengthEnum> eligibleLeaveLengthsForUnpaidLeave = new ArrayList<LeaveLengthEnum>();
            eligibleLeaveLengthsForUnpaidLeave.add(LeaveLengthEnum.FIVE_DAY_LEAVE);
            eligibleLeaveLengthsForUnpaidLeave.add(LeaveLengthEnum.FOURTEEN_DAY_LEAVE);
            eligibleLeaveLengthsForUnpaidLeave.add(LeaveLengthEnum.THIRTY_DAY_LEAVE);
            leaveTypeUnpaid.setEntitlement(eligibleLeaveLengthsForUnpaidLeave);
             */

            // eligible job designations: administrative, management

            /* TESTING
            List<JobDesignation> eligibleJDsForUnpaidLeaveType = new ArrayList<JobDesignation>();

            eligibleJDsForUnpaidLeaveType.add(jobDesignationService.findByName("management"));
            eligibleJDsForUnpaidLeaveType.add(jobDesignationService.findByName("administrative"));
            leaveTypeUnpaid.setEligibleJobDesignationsForLeaveType(eligibleJDsForUnpaidLeaveType);


             */
            leaveTypeRepository.save(leaveTypeUnpaid);

        }
    }

}
