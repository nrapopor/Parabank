package com.parasoft.parabank.web.controller;

import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.validator.AddressValidator;
import com.parasoft.parabank.domain.validator.CustomerValidator;
import com.parasoft.parabank.web.form.CustomerForm;

@SuppressWarnings("deprecation")
public abstract class AbstractCustomerControllerTest<T extends AbstractValidatingBankController>
extends AbstractValidatingBankControllerTest<T> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        CustomerValidator validator = new CustomerValidator();
        validator.setAddressValidator(new AddressValidator());
        controller.setValidator(validator);
    }
    
    public void testOnBindAndValidate() throws Exception {
        CustomerForm form = getCustomerForm();
        form.getCustomer().setFirstName(null);
        assertError(form, "customer.firstName");
        
        form = getCustomerForm();
        form.getCustomer().setLastName(null);
        assertError(form, "customer.lastName");
        
        form = getCustomerForm();
        form.getCustomer().getAddress().setStreet(null);
        assertError(form, "customer.address.street");
        
        form = getCustomerForm();
        form.getCustomer().getAddress().setCity(null);
        assertError(form, "customer.address.city");
        
        form = getCustomerForm();
        form.getCustomer().getAddress().setState(null);
        assertError(form, "customer.address.state");
        
        form = getCustomerForm();
        form.getCustomer().getAddress().setZipCode(null);
        assertError(form, "customer.address.zipCode");
    }
    
    protected CustomerForm getCustomerForm() throws Exception {
        CustomerForm form = createCustomerForm();
        form.getCustomer().setId(12212);
        form.getCustomer().setFirstName("first name");
        form.getCustomer().setLastName("last name");
        Address address = new Address();
        address.setStreet("customer street");
        address.setCity("customer city");
        address.setState("customer state");
        address.setZipCode("customer zipcode");
        form.getCustomer().setAddress(address);
        form.getCustomer().setPhoneNumber("phone number");
        form.getCustomer().setSsn("customer ssn");
        form.getCustomer().setUsername("customer username");
        form.getCustomer().setPassword("customer password");
        form.setRepeatedPassword("customer password");
        return form;
    }
    
    protected abstract CustomerForm createCustomerForm() throws Exception;
}
