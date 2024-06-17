package com.team4.leaveprocessingsystem.validator;

import com.team4.leaveprocessingsystem.model.Employee;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class HierarchyValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Employee.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Employee employee = (Employee) target;

        if (employee.getManager() != null) {
            if (employee.getManager().getId().equals(employee.getId())) {
                errors.rejectValue("manager",
                        "invalid.manager",
                        "Employee cannot be their own manager.");
            }
        }
    }
}
