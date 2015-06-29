package com.parasoft.parabank.domain.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.parasoft.parabank.domain.Address;

/**
 * Provides basic empty field validation for Address object
 */
public class AddressValidator implements Validator {
    public boolean supports(Class<?> clazz) {
        return Address.class.equals(clazz);
    }
    
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "street", "error.address.required");
        ValidationUtils.rejectIfEmpty(errors, "city", "error.city.required");
        ValidationUtils.rejectIfEmpty(errors, "state", "error.state.required");
        ValidationUtils.rejectIfEmpty(errors, "zipCode", "error.zip.code.required");
    }
}
