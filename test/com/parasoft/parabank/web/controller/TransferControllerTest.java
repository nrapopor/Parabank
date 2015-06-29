package com.parasoft.parabank.web.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.web.form.TransferForm;

@SuppressWarnings({"deprecation", "unchecked"})
public class TransferControllerTest
extends AbstractBankControllerTest<TransferController> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setCommandClass(TransferForm.class);
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
        TransferForm form = new TransferForm();
        form.setAmount(new BigDecimal(100));
        form.setFromAccountId(12345);
        form.setToAccountId(54321);
        
        ModelAndView mav = controller.onSubmit(form);
        assertEquals("transferConfirm", mav.getViewName());
        assertEquals(new BigDecimal(100), getModelValue(mav, "amount"));
        assertEquals(12345, getModelValue(mav, "fromAccountId"));
        assertEquals(54321, getModelValue(mav, "toAccountId"));
    }
        
    public void testOnBindAndValidate() throws Exception {
        TransferForm form = new TransferForm();
        form.setAmount(null);
        BindException errors = new BindException(form, "transferForm");
        controller.onBindAndValidate(request, form, errors);
        assertEquals(1, errors.getErrorCount());
        assertNotNull(errors.getFieldError("amount"));
    }
}
