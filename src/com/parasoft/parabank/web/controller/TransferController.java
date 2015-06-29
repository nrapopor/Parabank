package com.parasoft.parabank.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.TransferForm;

/**
 * Controller for transferring funds between accounts 
 */
@SuppressWarnings("deprecation")
public class TransferController extends AbstractBankController {
    
	private AccessModeController accessModeController ;
	private AdminManager adminManager;


	public void setAdminManager(AdminManager adminManager) {
	    this.adminManager = adminManager;
	}
	
	
	public void setAccessModeController(AccessModeController accessModeController) {
		this.accessModeController = accessModeController;
	}
	
	
	
	@Override    
    protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {       
        UserSession userSession = (UserSession)WebUtils.getRequiredSessionAttribute(request, "userSession");
        
        Customer customer = userSession.getCustomer();
        List<Account> accounts = bankManager.getAccountsForCustomer(customer);
        
        List<Integer> accountIds = new ArrayList<Integer>();
        for (Account account : accounts) {
            accountIds.add(account.getId());
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("accounts", accountIds);
        
        return model;
    }
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        ValidationUtils.rejectIfEmpty(errors, "amount", "error.amount.empty");
        super.onBindAndValidate(request, command, errors);
    }
       
    @Override
    protected ModelAndView onSubmit(Object command)
            throws Exception {
        TransferForm transferForm = (TransferForm)command;
        
        
        String accessMode = null;
        
        if (adminManager != null) {
        	accessMode = adminManager.getParameter("accessmode");
        }
        
        if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc") )
        {
        	accessModeController.doTransfer(transferForm.getFromAccountId(),transferForm.getToAccountId(), transferForm.getAmount());
        }
        
        else{
        	bankManager.transfer(transferForm.getFromAccountId(),transferForm.getToAccountId(), transferForm.getAmount());
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("amount", transferForm.getAmount());
        model.put("fromAccountId", transferForm.getFromAccountId());
        model.put("toAccountId", transferForm.getToAccountId());
        
        return new ModelAndView("transferConfirm", "model", model);
    }
}
