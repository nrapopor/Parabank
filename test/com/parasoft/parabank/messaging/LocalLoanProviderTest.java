package com.parasoft.parabank.messaging;

import java.math.BigDecimal;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.logic.impl.ConfigurableLoanProvider;
import com.parasoft.parabank.test.util.AbstractParaBankDataSourceTest;

@SuppressWarnings("deprecation")
public class LocalLoanProviderTest extends AbstractParaBankDataSourceTest {
    private static final String TEST_PROVIDER = "Test Provider";
    
    private LocalLoanProvider localLoanProvider;
    private ConfigurableLoanProvider loanProvider;
    
    public void setLoanProvider(ConfigurableLoanProvider loanProvider) {
        this.loanProvider = loanProvider;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        localLoanProvider = new LocalLoanProvider();
        localLoanProvider.setLoanProcessor(loanProvider);
        localLoanProvider.setLoanProviderName(TEST_PROVIDER);
    }

    public void testRequestLoan() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setAvailableFunds(new BigDecimal("1000.00"));
        loanRequest.setDownPayment(new BigDecimal("100.00"));
        loanRequest.setLoanAmount(new BigDecimal("5000.00"));
        
        LoanResponse response = localLoanProvider.requestLoan(loanRequest);
        assertTrue(response.isApproved());
        assertNotNull(response.getResponseDate());
        assertEquals(TEST_PROVIDER, response.getLoanProviderName());
    }
}
