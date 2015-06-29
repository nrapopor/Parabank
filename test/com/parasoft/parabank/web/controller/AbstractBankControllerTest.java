package com.parasoft.parabank.web.controller;

import org.springframework.mock.web.MockHttpSession;

import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.logic.BankManager;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;

abstract class AbstractBankControllerTest<T extends AbstractBankController>
extends AbstractControllerTest<T> {  
    protected BankManager bankManager;
    protected AccessModeController amc;
    
    public final void setBankManager(BankManager bankManager) {
        this.bankManager = bankManager;
    }
    
    public final void setAccessModeController(AccessModeController amc) {
        this.amc = amc;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setBankManager(bankManager);
    }
    
    protected final void registerSession() {
        MockHttpSession session = new MockHttpSession();
        Customer customer = new Customer();
        customer.setId(12212);
        session.setAttribute("userSession", new UserSession(customer));
        request.setSession(session);
    }
}