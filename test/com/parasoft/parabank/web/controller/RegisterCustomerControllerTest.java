package com.parasoft.parabank.web.controller;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.CustomerForm;

public class RegisterCustomerControllerTest
extends AbstractCustomerControllerTest<RegisterCustomerController> {   
    public void testOnSubmit() throws Exception {
        CustomerForm form = getCustomerForm();
        BindException errors = new BindException(form, "customerForm");
        ModelAndView mav = controller.onSubmit(request, response, form, errors);
        assertEquals("registerConfirm", mav.getViewName());
        Customer customer = (Customer)mav.getModel().get("customer");
        assertEquals(12434, customer.getId());
        
        UserSession session = (UserSession)request.getSession().getAttribute("userSession");
        assertNotNull(session);
        assertEquals(customer, session.getCustomer());
    }
    
    public void testDuplicateUsername() throws Exception {
        CustomerForm form = getCustomerForm();
        form.getCustomer().setUsername("john");
        BindException errors = new BindException(form, "customerForm");
        controller.onSubmit(request, response, form, errors);
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("customer.username"));
    }
    
    public void testOnBindAndValidate() throws Exception {
        super.testOnBindAndValidate();
        
        CustomerForm form = getCustomerForm();
        form.setRepeatedPassword(null);
        assertError(form, "repeatedPassword");
        
        form = getCustomerForm();
        form.setRepeatedPassword("password");
        assertError(form, "repeatedPassword");
    }
    
    protected CustomerForm createCustomerForm() throws Exception {
        return (CustomerForm)controller.formBackingObject(request);
    }
}
