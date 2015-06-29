package com.parasoft.parabank.web.controller;

import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;

@SuppressWarnings({"deprecation", "unchecked"})
public class AccountActivityControllerTest
extends AbstractBankControllerTest<AccountActivityController> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setCommandClass(TransactionCriteria.class);
    }
    
    public void testHandleGetRequest() throws Exception {
        request.setParameter("id", "12345");
        ModelAndView mav = controller.handleRequest(request, response);
        
        List<Transaction> transactions = 
            (List<Transaction>)getModelValue(mav, "transactions");
        
        assertEquals(7, transactions.size());
        
        assertReferenceData(mav);
    }
    
    private void assertReferenceData(ModelAndView mav) {
        List<String> months = (List<String>)mav.getModel().get("months");
        assertEquals(13, months.size());
        
        List<String> types = (List<String>)mav.getModel().get("types");
        assertEquals(3, types.size());
    }
    
    public void testHandleInvalidGetRequest() throws Exception {
        assertInvalidRequest();
    }
    
    private void assertInvalidRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        
        assertEquals("error", mav.getViewName());
        assertEquals("error.missing.account.id", getModelValue(mav, "message"));
        
        request.setParameter("id", "str");
        mav = controller.handleRequest(request, response);
        
        assertEquals("error", mav.getViewName());
        assertEquals("error.invalid.account.id", getModelValue(mav, "message"));
        
        request.setParameter("id", "0");
        mav = controller.handleRequest(request, response);
        assertEquals("error.invalid.account.id", getModelValue(mav, "message"));
    }
    
    public void testHandlePostRequest() throws Exception {
        request.setMethod("POST");
        request.setParameter("id", "12345");
        assertTransactions(7);
        
        request.setParameter("transactionType", "Credit");
        assertTransactions(1);
        
        request.setParameter("transactionType", "Debit");
        assertTransactions(6);
        
        request.setParameter("transactionType", "All");
        request.setParameter("Month", "December");
        assertTransactions(2);
        
        request.setParameter("transactionType", "All");
        request.setParameter("Month", "All");
        assertTransactions(7);
    }
    
    private void assertTransactions(int expectedSize) throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        List<Transaction> transactions = (List<Transaction>)getModelValue(mav, "transactions");
        assertEquals(expectedSize, transactions.size());
        assertReferenceData(mav);
    }
    
    public void testHandleInvalidPostRequest() throws Exception {
        request.setMethod("POST");
        assertInvalidRequest();
    }
}
