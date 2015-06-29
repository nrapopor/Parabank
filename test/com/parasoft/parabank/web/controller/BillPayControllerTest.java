package com.parasoft.parabank.web.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.Payee;
import com.parasoft.parabank.domain.validator.AddressValidator;
import com.parasoft.parabank.domain.validator.PayeeValidator;
import com.parasoft.parabank.web.form.BillPayForm;

@SuppressWarnings({"deprecation", "unchecked"})
public class BillPayControllerTest
extends AbstractValidatingBankControllerTest<BillPayController> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        PayeeValidator validator = new PayeeValidator();
        validator.setAddressValidator(new AddressValidator());
        controller.setValidator(validator);
        controller.setMessageSource(getApplicationContext());
        registerSession();
    }
    
    public void testHandleGetRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        assertReferenceData(mav);
    }
    
    private void assertReferenceData(ModelAndView mav) {
        List<Account> accounts = (List<Account>)mav.getModel().get("accounts");
        assertEquals(11, accounts.size());
    }
    
    public void testOnSubmit() throws Exception {
        BillPayForm form = getBillPayForm();
        BindException errors = new BindException(form, "billPayForm");
        ModelAndView mav = controller.onSubmit(request, response, form, errors);
        assertEquals("billpayConfirm", mav.getViewName());
        assertEquals("payee name", getModelValue(mav, "payeeName"));
        assertEquals(new BigDecimal("100.0"), getModelValue(mav, "amount"));
        assertEquals(12345, getModelValue(mav, "fromAccountId"));
    }
    
    public void testOnBindAndValidate() throws Exception {
        BillPayForm form = getBillPayForm();
        form.setAmount(null);
        assertError(form, "amount");
        
        form = getBillPayForm();
        form.setVerifyAccount(200);
        assertError(form, "verifyAccount");
    }
    
    private BillPayForm getBillPayForm() {
        BillPayForm form = new BillPayForm();
        Payee payee = new Payee();
        payee.setName("payee name");
        Address address = new Address();
        address.setStreet("payee street");
        address.setCity("payee city");
        address.setState("payee state");
        address.setZipCode("payee zipcode");
        payee.setAddress(address);
        payee.setPhoneNumber("payee phone number");
        payee.setAccountNumber(100);
        form.setPayee(payee);
        form.setVerifyAccount(100);
        form.setAmount(new BigDecimal("100.0"));
        form.setFromAccountId(12345);
        return form;
    }
}
