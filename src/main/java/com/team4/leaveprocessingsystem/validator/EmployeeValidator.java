package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.Employee;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class EmployeeValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Employee.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Employee employee = (Employee) target;

        /*
        Ensure
        - an Employee cannot be saved without being assigned a Manager
        - a Manager can be saved without being assigned a Manager
        there should be an Option to assign them to an existing Manager or None before saving
        */
        if (!Objects.equals(employee.getJobDesignation().getName(),"management") &&
                employee.getManager().getId() == null) {
            errors.rejectValue("manager",
                    "invalid.manager",
                    "Employee needs to be assigned a manager, to be saved.");
        }

        /*
        if (employee.getJobDesignation().getName() != "management" && employee.getManager() == null) {
            errors.rejectValue("manager",
                    "invalid.manager",
                    "Employee needs to be assigned a manager, to be saved.");
        }
        */

    }
}
