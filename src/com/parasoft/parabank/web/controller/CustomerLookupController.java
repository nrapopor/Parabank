package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.ViewUtil;
import com.parasoft.parabank.web.form.LookupForm;

/**
 * Controller for retrieving lost customer signin info
 */
@SuppressWarnings("deprecation")
public class CustomerLookupController extends AbstractValidatingBankController {
    private static final Log log = LogFactory.getLog(CustomerLookupController.class);
    
    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        return new LookupForm();
    }
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        LookupForm lookupForm = (LookupForm)command;
        Address address = lookupForm.getAddress();
        
        try {
            errors.pushNestedPath("address");
            getValidator().validate(address, errors);
        } finally {
            errors.popNestedPath();
        }
        
        ValidationUtils.rejectIfEmpty(errors, "firstName", "error.first.name.required");
        ValidationUtils.rejectIfEmpty(errors, "lastName", "error.last.name.required");
        ValidationUtils.rejectIfEmpty(errors, "ssn", "error.ssn.required");
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        LookupForm lookupForm = (LookupForm)command;
        
        String ssn = lookupForm.getSsn();
        Customer customer = bankManager.getCustomer(ssn);
        if (customer == null) {
            log.error("Invalid SSN = " + ssn);
            return ViewUtil.createErrorView("error.invalid.ssn");
        }
        
        UserSession userSession = new UserSession(customer);
        request.getSession().setAttribute("userSession", userSession);
        
        return new ModelAndView("lookupConfirm", "customer", customer); 
    }
}
