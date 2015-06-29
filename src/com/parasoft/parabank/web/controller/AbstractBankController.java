package com.parasoft.parabank.web.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.parasoft.parabank.domain.logic.BankManager;

/**
 * Abstract controller that depends on banking functions
 */
@SuppressWarnings("deprecation")
abstract class AbstractBankController extends SimpleFormController {
    protected BankManager bankManager;
    
    public final void setBankManager(BankManager bankManager) {
        this.bankManager = bankManager;
    }
}
