package com.team4.leaveprocessingsystem.interfacemethods;

import org.springframework.validation.Errors;

public interface IValidator {
    boolean supports(Class<?> clazz);
    void validate(Object target, Errors errors);
}
