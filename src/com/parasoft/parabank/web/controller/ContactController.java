package com.parasoft.parabank.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.web.form.ContactForm;

/**
 * Controller for collecting customer support messages (currently ignores the message)
 */
public class ContactController extends AbstractValidatingBankController {
    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        ValidationUtils.rejectIfEmpty(errors, "name", "error.name.empty");
        ValidationUtils.rejectIfEmpty(errors, "email", "error.email.empty");
        ValidationUtils.rejectIfEmpty(errors, "phone", "error.phone.empty");
        ValidationUtils.rejectIfEmpty(errors, "message", "error.message.empty");
    }
    
    @Override
    protected ModelAndView onSubmit(Object command)
            throws Exception {
        ContactForm customerServiceForm = (ContactForm)command;
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", customerServiceForm.getName());
        
        return new ModelAndView("contactConfirm", "model", model);
    }
}
