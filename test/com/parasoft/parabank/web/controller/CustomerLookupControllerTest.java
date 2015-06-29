package com.parasoft.parabank.web.controller;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.validator.AddressValidator;
import com.parasoft.parabank.web.form.LookupForm;

@SuppressWarnings("deprecation")
public class CustomerLookupControllerTest
extends AbstractValidatingBankControllerTest<CustomerLookupController> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setValidator(new AddressValidator());
    }
    
    public void testHandlePostRequest() throws Exception {
        LookupForm form = getLookupForm();
        BindException errors = new BindException(form, "lookupForm");
        ModelAndView mav = controller.onSubmit(request, response, form, errors);
        assertEquals("lookupConfirm", mav.getViewName());
        Customer customer = (Customer)mav.getModel().get("customer");
        assertEquals(12212, customer.getId());
        
        form = getLookupForm();
        form.setSsn("invalid");
        errors = new BindException(form, "lookupForm");
        mav = controller.onSubmit(request, response, form, errors);
        assertEquals("error", mav.getViewName());
        assertEquals("error.invalid.ssn", getModelValue(mav, "message"));
    }
    
    public void testOnBindAndValidate() throws Exception {
        LookupForm form = getLookupForm();
        form.setFirstName(null);
        assertError(form, "firstName");
        
        form = getLookupForm();
        form.setLastName(null);
        assertError(form, "lastName");
        
        form = getLookupForm();
        form.setSsn(null);
        assertError(form, "ssn");
    }
    
    private LookupForm getLookupForm() throws Exception {
        LookupForm form = (LookupForm)controller.formBackingObject(request);
        form.setFirstName("first name");
        form.setLastName("last name");
        Address address = new Address();
        address.setStreet("customer street");
        address.setCity("customer city");
        address.setState("customer state");
        address.setZipCode("customer zip code");
        form.setAddress(address);
        form.setSsn("622-11-9999");
        return form;
    }
}
