package com.parasoft.parabank.service;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.logic.LoanProvider;

public class LoanProcessorServiceImpl implements LoanProcessorService {
    private LoanProvider loanProcessor;
    private String loanProviderName;
    
    public void setLoanProcessor(LoanProvider loanProcessor) {
        this.loanProcessor = loanProcessor;
    }
    
    public void setLoanProviderName(String loanProviderName) {
        this.loanProviderName = loanProviderName;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.service.LoanProcessorService#requestLoan(com.parasoft.parabank.domain.LoanRequest)
     */
    public LoanResponse requestLoan(LoanRequest loanRequest)
            throws ParaBankServiceException {
        LoanResponse response = loanProcessor.requestLoan(loanRequest);
        response.setLoanProviderName(loanProviderName);
        return response;
    }
}
