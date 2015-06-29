package com.parasoft.parabank.domain.logic.impl;

import java.util.Map;
import java.util.Set;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.domain.logic.LoanProvider;

/**
 * Aggregate LoanProvider than can change behavior based on a parameter value
 */
public class ConfigurableLoanProvider implements LoanProvider {
    private AdminManager adminManager;
    private Map<String, LoanProvider> loanProviders;
    private String parameter;
    
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public void setLoanProviders(Map<String, LoanProvider> loanProviders) {
        this.loanProviders = loanProviders;
    }
    
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
    public LoanResponse requestLoan(LoanRequest loanRequest) {
        return getLoanProvider().requestLoan(loanRequest);
    }
    
    public Set<String> getProviderNames() {
        return loanProviders.keySet();
    }
    
    private LoanProvider getLoanProvider() {
        String type = adminManager.getParameter(parameter);
        return loanProviders.get(type);
    }
}
