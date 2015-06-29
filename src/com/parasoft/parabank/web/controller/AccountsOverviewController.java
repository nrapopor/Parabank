package com.parasoft.parabank.web.controller;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;


/**
 * Controller for displaying all user accounts
 */
public class AccountsOverviewController extends AbstractBankController {
	
	
private AccessModeController accessModeController;
private AdminManager adminManager;
	
	public void setAccessModeController(AccessModeController accessModeController) {
		this.accessModeController = accessModeController;
	}
	
	
	public void setAdminManager(AdminManager adminManager) {
	    this.adminManager = adminManager;
	}
		

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	
    	final Log log = LogFactory.getLog(AccountsOverviewController.class);
        UserSession userSession = (UserSession)WebUtils.getRequiredSessionAttribute(request, "userSession");
        
        Customer customer = userSession.getCustomer();
        
        List<Account> accounts= new ArrayList<Account>();
   	 
        String accessMode = null;
        
        if (adminManager != null) {
        	accessMode = adminManager.getParameter("accessmode");
        }
        
        if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc") )
        {	
        	accounts = accessModeController.doGetAccounts(customer);
        }
        else
        {
        	accounts = bankManager.getAccountsForCustomer(customer);
			log.warn("Using regular JDBC connection");
        }
            
        BigDecimal totalBalance = BigDecimal.ZERO;
        BigDecimal totalAvailableBalance = BigDecimal.ZERO;

        for (Account account : accounts) {
            totalBalance = totalBalance.add(account.getBalance());
            totalAvailableBalance = totalAvailableBalance.add(account.getAvailableBalance());
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("accounts", accounts);
        model.put("totalBalance", totalBalance);
        model.put("totalAvailableBalance", totalAvailableBalance);
        
        return new ModelAndView("overview", "model", model);
    }
}
