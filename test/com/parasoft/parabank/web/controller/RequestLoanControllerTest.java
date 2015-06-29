package com.parasoft.parabank.web.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.web.form.RequestLoanForm;

@SuppressWarnings({"deprecation", "unchecked"})
public class RequestLoanControllerTest
extends AbstractBankControllerTest<RequestLoanController> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setCommandClass(RequestLoanForm.class);
        registerSession();
    }
    
    public void testHandleGetRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);

        List<Account> accounts = (List<Account>)mav.getModel().get("accounts");
        assertEquals(11, accounts.size());
    }
    
    public void testOnSubmit() throws Exception {
        final int FROM_ACCOUNT_ID = 12345;
        
        RequestLoanForm form = new RequestLoanForm();
        form.setAmount(new BigDecimal("1000.00"));
        form.setDownPayment(new BigDecimal("100.00"));
        form.setFromAccountId(FROM_ACCOUNT_ID);
        
        BindException errors = new BindException(form, "requestLoanForm");
        ModelAndView mav = controller.onSubmit(request, response, form, errors);
        assertEquals("requestloanConfirm", mav.getViewName());
        
        LoanResponse response = (LoanResponse)mav.getModel().get("loanResponse");
        assertTrue(response.isApproved());
        assertNotNull(response.getResponseDate());
        assertTrue(response.getAccountId() > FROM_ACCOUNT_ID);
    }
        
    public void testOnBindAndValidate() throws Exception {
        RequestLoanForm form = new RequestLoanForm();
        form.setAmount(null);
        BindException errors = new BindException(form, "requestLoanForm");
        controller.onBindAndValidate(request, form, errors);
        assertEquals(2, errors.getErrorCount());
        assertNotNull(errors.getFieldError("amount"));
        assertNotNull(errors.getFieldError("downPayment"));
    }
}
