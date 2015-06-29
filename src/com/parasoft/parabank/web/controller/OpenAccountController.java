package com.parasoft.parabank.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Account.AccountType;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.domain.logic.AdminParameters;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.OpenAccountForm;

/**
 * Controller for creating a new bank account
 */
public class OpenAccountController extends AbstractBankController {
    private AdminManager adminManager;
    private AccessModeController accessModeController;
    
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    
    public void setAccessModeController(AccessModeController accessModeController) {
    	this.accessModeController = accessModeController;
    }
    
    
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
        UserSession userSession = (UserSession) WebUtils.getRequiredSessionAttribute(request, "userSession");
        
        Customer customer = userSession.getCustomer();
        List<Account> accounts = bankManager.getAccountsForCustomer(customer);
        
        List<Integer> accountIds = new ArrayList<Integer>();
        for (Account account : accounts) {
            accountIds.add(account.getId());
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("minimumBalance", adminManager.getParameter(AdminParameters.MINIMUM_BALANCE));
        model.put("accounts", accountIds);
        List<AccountType> types = new ArrayList<AccountType>();
        for (AccountType type : AccountType.values()) {
            if (!type.isInternal()) {
                types.add(type);
            }
        }
        model.put("types", types);
        
        return model;
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        
    	final Log log = LogFactory.getLog(OpenAccountController.class);
        OpenAccountForm openAccountForm = (OpenAccountForm) command;
        
        UserSession userSession = (UserSession)WebUtils.getRequiredSessionAttribute(request, "userSession");
        
        Account newAccount;
        
        String accessMode = null;
        
        if (adminManager != null) {
        	accessMode = adminManager.getParameter("accessmode");
        }
        
        if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc") )
        {	
        	newAccount = accessModeController.createAccount(userSession.getCustomer().getId(), 
        							openAccountForm.getType().ordinal(), openAccountForm.getFromAccountId());
        }
        else
        {
        	newAccount = new Account();
            newAccount.setCustomerId(userSession.getCustomer().getId());
            newAccount.setType(openAccountForm.getType());
            newAccount.setBalance(BigDecimal.ZERO);
        	bankManager.createAccount(newAccount, openAccountForm.getFromAccountId());
			log.warn("Using regular JDBC connection");
        }
        
        
        return new ModelAndView("openaccountConfirm", "account", newAccount);
    }
}
