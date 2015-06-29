package com.parasoft.parabank.web.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.TransactionCriteria.SearchType;
import com.parasoft.parabank.domain.validator.TransactionCriteriaValidator;
import com.parasoft.parabank.web.form.FindTransactionForm;

@SuppressWarnings({"deprecation", "unchecked"})
public class FindTransactionControllerTest
extends AbstractValidatingBankControllerTest<FindTransactionController> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setValidator(new TransactionCriteriaValidator());
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
        FindTransactionForm form = getFindTransactionForm();
        form.getCriteria().setSearchType(SearchType.ACTIVITY);
        assertTransactions(form, 7);
        
        form = getFindTransactionForm();
        form.getCriteria().setSearchType(SearchType.ID);
        form.getCriteria().setTransactionId(14143);
        assertTransactions(form, 1);
        
        form = getFindTransactionForm();
        form.getCriteria().setSearchType(SearchType.DATE);
        form.getCriteria().setOnDate(new Date(110, 7, 23));
        assertTransactions(form, 2);
        
        form = getFindTransactionForm();
        form.getCriteria().setSearchType(SearchType.DATE_RANGE);
        form.getCriteria().setFromDate(new Date(110, 7, 1));
        form.getCriteria().setToDate(new Date(110, 7, 31));
        assertTransactions(form, 5);
        
        form = getFindTransactionForm();
        form.getCriteria().setSearchType(SearchType.AMOUNT);
        form.getCriteria().setAmount(new BigDecimal(1000));
        assertTransactions(form, 3);
    }
    
    public void testOnBindAndValidate() throws Exception {
        FindTransactionForm form = getFindTransactionForm();
        form.getCriteria().setSearchType(SearchType.ID);
        form.getCriteria().setTransactionId(null);
        assertError(form, "criteria.transactionId");
        
        form.getCriteria().setSearchType(SearchType.DATE);
        form.getCriteria().setOnDate(null);
        assertError(form, "criteria.onDate");
        
        form.getCriteria().setSearchType(SearchType.DATE_RANGE);
        form.getCriteria().setFromDate(null);
        form.getCriteria().setToDate(null);
        assertError(form, "criteria.fromDate", "criteria.toDate");
        
        form.getCriteria().setSearchType(SearchType.AMOUNT);
        form.getCriteria().setOnDate(null);
        assertError(form, "criteria.amount");
    }
    
    public void assertTransactions(FindTransactionForm form, int expectedSize) throws Exception {
        ModelAndView mav = controller.onSubmit(form);
        assertEquals("transactionResults", mav.getViewName());
        List<Transaction> transactions = (List<Transaction>)getModelValue(mav, "transactions");
        assertEquals(expectedSize, transactions.size());
    }
    
    private FindTransactionForm getFindTransactionForm() {
        FindTransactionForm form = new FindTransactionForm();
        form.setAccountId(12345);
        TransactionCriteria criteria = new TransactionCriteria();
        form.setCriteria(criteria);
        return form;
    }
}
