package com.parasoft.parabank.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.FindTransactionForm;

/**
 * Controller for searching transactions
 */
@SuppressWarnings("deprecation")
public class FindTransactionController extends AbstractValidatingBankController {
	private  List<Transaction> transactions = null;
	private AccessModeController accessModeController ;
	private AdminManager adminManager;


	public void setAdminManager(AdminManager adminManager) {
	    this.adminManager = adminManager;
	}
		
		
		public void setAccessModeController(AccessModeController accessModeController) {
			this.accessModeController = accessModeController;
		}
		
		
	
	
	
    @Override
    protected void initBinder(HttpServletRequest request,
            ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                new SimpleDateFormat("MM-dd-yyyy"), true));
    }
    
    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        return new FindTransactionForm();
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
        FindTransactionForm findTransactionForm = (FindTransactionForm)command;

        TransactionCriteria criteria = findTransactionForm.getCriteria();
        
        try {
            errors.pushNestedPath("criteria");
            getValidator().validate(criteria, errors);
        } finally {
            errors.popNestedPath();
        }
    }
    
    @Override
    protected ModelAndView onSubmit(Object command)
            throws Exception {
        FindTransactionForm findTransactionForm = (FindTransactionForm)command;
        
        Account account = bankManager.getAccount(findTransactionForm.getAccountId());
        String accessMode = null; 
        
        if (adminManager != null) {
        	accessMode = adminManager.getParameter("accessmode");
        }
        
        if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc") )
        {
        	Integer transactionId = findTransactionForm.getCriteria().getTransactionId();

        	transactions = accessModeController.getTransactionsForAccount(account,
    												findTransactionForm.getCriteria());
        }
        else{
        	transactions = bankManager.getTransactionsForAccount(account.getId(),
        	                    				findTransactionForm.getCriteria());    
        }
                
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("transactions", transactions);
        
        return new ModelAndView("transactionResults", "model", model); 
    }
}
