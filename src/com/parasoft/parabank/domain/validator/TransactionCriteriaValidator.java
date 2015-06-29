package com.parasoft.parabank.domain.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.parasoft.parabank.domain.TransactionCriteria;

/**
 * Provides basic empty field validation for TransactionCriteria object
 */
public class TransactionCriteriaValidator implements Validator {
    public boolean supports(Class<?> clazz) {
        return TransactionCriteria.class.equals(clazz);
    }
    
    public void validate(Object obj, Errors errors) {
        TransactionCriteria criteria = (TransactionCriteria)obj;
        switch (criteria.getSearchType()) {
        case ID:
            ValidationUtils.rejectIfEmpty(errors, "transactionId", "error.empty.transaction.id");
            break;
        case DATE:
            ValidationUtils.rejectIfEmpty(errors, "onDate", "error.empty.on.date");
            break;
        case DATE_RANGE:
            ValidationUtils.rejectIfEmpty(errors, "fromDate", "error.empty.from.date");
            ValidationUtils.rejectIfEmpty(errors, "toDate", "error.empty.to.date");
            break;
        case AMOUNT:
            ValidationUtils.rejectIfEmpty(errors, "amount", "error.empty.amount");
        }
    }
}
