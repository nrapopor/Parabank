package com.parasoft.parabank.domain.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.parasoft.parabank.domain.Customer;

/**
 * Provides basic empty field validation for Customer object
 */
public class CustomerValidator implements Validator {
    private Validator addressValidator;
    
    public void setAddressValidator(Validator addressValidator) {
        this.addressValidator = addressValidator;
    }
    
    public boolean supports(Class<?> clazz) {
        return Customer.class.equals(clazz);
    }
    
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "firstName", "error.first.name.required");
        ValidationUtils.rejectIfEmpty(errors, "lastName", "error.last.name.required");
        ValidationUtils.rejectIfEmpty(errors, "ssn", "error.ssn.required");
        ValidationUtils.rejectIfEmpty(errors, "username", "error.username.required");
        ValidationUtils.rejectIfEmpty(errors, "password", "error.password.required");
        
        Customer customer = (Customer)obj;
        try {
            errors.pushNestedPath("address");
            ValidationUtils.invokeValidator(addressValidator, customer.getAddress(), errors);
        } finally {
            errors.popNestedPath();
        }
    }
}
