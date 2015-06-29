package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.util.Util;
import com.parasoft.parabank.web.ViewUtil;

/**
 * Controller for displaying transaction details
 */
public class TransactionController extends AbstractBankController {
	
	private AccessModeController accessModeController;
	private AdminManager adminManager;
	private static final Log log = LogFactory.getLog(TransactionController.class);


	public void setAdminManager(AdminManager adminManager) {
	    this.adminManager = adminManager;
	}
		
		
	public void setAccessModeController(AccessModeController accessModeController) {
		this.accessModeController = accessModeController;
	}

    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	
        String id = request.getParameter("id");
        if (Util.isEmpty(id)) {
            log.error("Missing required transaction id");
            return ViewUtil.createErrorView("error.missing.transaction.id");
        }
        
        Transaction transaction = null;
        try {
        	
        	String accessMode = null;
        	
        	if (adminManager != null) {
        		accessMode = adminManager.getParameter("accessmode");
        	}
        	 
        	if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc") )
            {
            	transaction =  accessModeController.doGetTransaction(Integer.parseInt(id));
            }
            
            else{
            	
            	transaction = bankManager.getTransaction(Integer.parseInt(id));
            	 log.info("Using regular JDBC connection");
            }
            
        } catch (NumberFormatException e) {
            log.error("Invalid transaction id = " + id, e);
            return ViewUtil.createErrorView("error.invalid.transaction.id",
                    new Object[] { request.getParameter("id") });
        } catch (DataAccessException e) {
            log.error("Invalid transaction id = " + id, e);
            return ViewUtil.createErrorView("error.invalid.transaction.id",
                    new Object[] { request.getParameter("id") });
        }
        
        return new ModelAndView("transaction", "transaction", transaction); 
    }
}
