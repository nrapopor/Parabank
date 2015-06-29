package com.parasoft.parabank.domain.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.parasoft.parabank.domain.Payee;

/**
 * Provides basic empty field validation for Payee object
 */
public class PayeeValidator implements Validator {
    private Validator addressValidator;
    
    public void setAddressValidator(Validator addressValidator) {
        this.addressValidator = addressValidator;
    }
    
    public boolean supports(Class<?> clazz) {
        return Payee.class.equals(clazz);
    }
    
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "error.payee.name.required");
        ValidationUtils.rejectIfEmpty(errors, "phoneNumber", "error.phone.number.required");
        ValidationUtils.rejectIfEmpty(errors, "accountNumber", "error.account.number.required");
        
        Payee payee = (Payee)obj;
        try {
            errors.pushNestedPath("address");
            ValidationUtils.invokeValidator(addressValidator, payee.getAddress(), errors);
        } finally {
            errors.popNestedPath();
        }
    }
}
