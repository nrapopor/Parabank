package com.parasoft.parabank.web.controller;

import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Transaction;

public class TransactionControllerTest
extends AbstractBankControllerTest<TransactionController> {
    public void testHandleRequest() throws Exception {
        request.setParameter("id", "12367");
        ModelAndView mav = controller.handleRequest(request, response);
        
        assertEquals("transaction", mav.getViewName());
        Transaction transaction = (Transaction)mav.getModel().get("transaction");
        assertEquals(12367, transaction.getId());
    }
        
    public void testHandleInvalidRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        
        assertEquals("error", mav.getViewName());
        assertEquals("error.missing.transaction.id", getModelValue(mav, "message"));
        
        request.setParameter("id", "str");
        mav = controller.handleRequest(request, response);
        
        assertEquals("error", mav.getViewName());
        assertEquals("error.invalid.transaction.id", getModelValue(mav, "message"));
        
        request.setParameter("id", "0");
        mav = controller.handleRequest(request, response);
        assertEquals("error.invalid.transaction.id", getModelValue(mav, "message"));
    }
}
