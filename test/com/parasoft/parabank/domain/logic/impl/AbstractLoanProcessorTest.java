package com.parasoft.parabank.domain.logic.impl;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public abstract class AbstractLoanProcessorTest<T extends AbstractLoanProcessor>
extends AbstractParaBankDataSourceTest {
    private Class<T> processorClass;
    protected T processor;
    
    private AdminManager adminManager;
    
    protected LoanRequest loanRequest;
    
    @SuppressWarnings("unchecked")
    protected AbstractLoanProcessorTest() {
        processorClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        
        processor = processorClass.newInstance();
        processor.setAdminManager(adminManager);
        
        loanRequest = new LoanRequest();
        loanRequest.setAvailableFunds(new BigDecimal("1000.00"));
    }
    
    public final void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public final void testProcessLoan() {
        loanRequest.setLoanAmount(new BigDecimal("10000.00"));
        loanRequest.setDownPayment(new BigDecimal("2000.00"));
        LoanResponse response = processor.requestLoan(loanRequest);
        assertFalse(response.isApproved());
        assertNotNull(response.getResponseDate());
        assertEquals("error.insufficient.funds.for.down.payment", response.getMessage());
        assertProcessor();
    }
    
    protected abstract void assertProcessor();
}
