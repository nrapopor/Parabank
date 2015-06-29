package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.CustomerForm;

/**
 * Controller for creating a new bank customer
 */
@SuppressWarnings("deprecation")
public class RegisterCustomerController extends AbstractValidatingBankController {
    private static final Log log = LogFactory.getLog(RegisterCustomerController.class);
    
    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        return new CustomerForm();
    }
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        CustomerForm customerForm = (CustomerForm)command;
        Customer customer = customerForm.getCustomer();
        
        try {
            errors.pushNestedPath("customer");
            getValidator().validate(customer, errors);
        } finally {
            errors.popNestedPath();
        }
        
        if (customerForm.getRepeatedPassword() == null || customerForm.getRepeatedPassword().length() <= 0) {
            errors.rejectValue("repeatedPassword", "error.password.confirmation.required");
        } else if (customerForm.getCustomer().getPassword() != null && customerForm.getCustomer().getPassword().length() > 0 &&
                !customer.getPassword().equals(customerForm.getRepeatedPassword())) {
            errors.rejectValue("repeatedPassword", "error.password.mismatch");
        }
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        CustomerForm customerForm = (CustomerForm)command;
        try {
            bankManager.createCustomer(customerForm.getCustomer());
        } catch (DataIntegrityViolationException ex) {
            log.warn("Username " + customerForm.getCustomer().getUsername() + " already exists in database");
            errors.rejectValue("customer.username", "error.username.already.exists");
            return showForm(request, response, errors);
        }
        
        UserSession userSession = new UserSession(bankManager.getCustomer(customerForm.getCustomer().getId()));
        request.getSession().setAttribute("userSession", userSession);
        
        return new ModelAndView("registerConfirm", "customer", customerForm.getCustomer());
    }
}
