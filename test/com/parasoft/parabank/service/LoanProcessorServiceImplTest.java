package com.parasoft.parabank.service;

import java.math.BigDecimal;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class LoanProcessorServiceImplTest extends AbstractParaBankDataSourceTest {    
    private LoanProcessorService loanProcessorService;
    private LoanRequest loanRequest;

    public void setLoanProcessorService(LoanProcessorService loanProcessorService) {
        this.loanProcessorService = loanProcessorService;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        loanRequest = new LoanRequest();
        loanRequest.setCustomerId(12345);
        loanRequest.setAvailableFunds(new BigDecimal("1000.00"));
    }
    
    public void testRequestLoan() throws Exception {
        loanRequest.setLoanAmount(new BigDecimal("100.00"));
        loanRequest.setDownPayment(new BigDecimal("10.00"));
        LoanResponse response = loanProcessorService.requestLoan(loanRequest);
        assertTrue(response.isApproved());
        assertNotNull(response.getResponseDate());
        assertEquals("Test Provider", response.getLoanProviderName());
        
        loanRequest.setLoanAmount(new BigDecimal("10000.00"));
        response = loanProcessorService.requestLoan(loanRequest);
        assertFalse(response.isApproved());
        assertNotNull(response.getResponseDate());
        assertEquals("error.insufficient.funds", response.getMessage());
        assertEquals("Test Provider", response.getLoanProviderName());
    }
}
