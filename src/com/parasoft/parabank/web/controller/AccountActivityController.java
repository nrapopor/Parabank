package com.parasoft.parabank.web.controller;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.Transaction.TransactionType;
import com.parasoft.parabank.domain.TransactionCriteria.SearchType;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.service.ParaBankServiceException;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.util.Util;
import com.parasoft.parabank.web.ViewUtil;

/**
 * Controller for displaying user account activity
 */
@SuppressWarnings("deprecation")
public class AccountActivityController extends AbstractBankController {
	
	private AccessModeController accessModeController;
	private AdminManager adminManager;


	public void setAdminManager(AdminManager adminManager) {
	    this.adminManager = adminManager;
	}
		
		
	public void setAccessModeController(AccessModeController accountActivityController) {
		this.accessModeController = accountActivityController;
	}

		
    private static final Log log = LogFactory.getLog(AccountActivityController.class);
    
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {       
        Map<String, Object> model = new HashMap<String, Object>();
        
        List<String> months = new ArrayList<String>(Arrays.asList(new DateFormatSymbols(request.getLocale()).getMonths()));
        months.add(0, "All");
        months.remove(13);
        
        List<String> types = new ArrayList<String>();
        for (TransactionType type : TransactionType.values()) {
            types.add(type.toString());
        }
        types.add(0, "All");
        
        model.put("months", months);
        model.put("types", types);
        
        return model;
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {
        ModelAndView modelAndView = super.showForm(request, response, errors);
        
        return loadTransactions(request, null, modelAndView);        
    };
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        ModelAndView modelAndView = super.showForm(request, response, errors);
        
        TransactionCriteria criteria = (TransactionCriteria)command;
        criteria.setSearchType(SearchType.ACTIVITY);
        
        return loadTransactions(request, criteria, modelAndView);        
    }
    
    private ModelAndView loadTransactions(HttpServletRequest request,
            TransactionCriteria criteria, ModelAndView modelAndView) throws ParaBankServiceException, IOException, JAXBException {
    	
    	Account account;
    	List<Transaction> transactions;
        Map<String, Object> model = new HashMap<String, Object>();
        
        String id = request.getParameter("id");
        if (Util.isEmpty(id)) {
            log.error("Missing required account id");
            return ViewUtil.createErrorView("error.missing.account.id");
        }
        
        try {
        	
        	String accessMode = null;
        	
        	if (adminManager != null) {
        		accessMode = adminManager.getParameter("accessmode");
        	}
            
        	if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc") )
            {
            	account = accessModeController.doGetAccount(Integer.parseInt(id));
            	transactions = accessModeController.getTransactionsForAccount(account, criteria);
            }
            
            else{
            	 // default JDBC
            	 account = bankManager.getAccount(Integer.parseInt(id));
            	 
            	 transactions = criteria == null ?
                         bankManager.getTransactionsForAccount(account) :
                             bankManager.getTransactionsForAccount(account.getId(), criteria);
            }
        	
            
        
                    model.put("account", account);
                    model.put("transactions", transactions);
                    
                    modelAndView.addObject("model", model);
        } catch (NumberFormatException e) {
            log.error("Invalid account id = " + id, e);
            return ViewUtil.createErrorView("error.invalid.account.id", 
                    new Object[] { request.getParameter("id") });
        } catch (DataAccessException e) {
            log.error("Invalid account id = " + id, e);
            return ViewUtil.createErrorView("error.invalid.account.id", 
                    new Object[] { request.getParameter("id") });
        } catch (ParseException e) {
        	log.error("Error retrieving transactions for account " + id);
			throw new ParaBankServiceException("Error retreiving transactions");
		}
        
        return modelAndView;
    }
}
