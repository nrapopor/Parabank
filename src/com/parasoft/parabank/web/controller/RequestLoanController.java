package com.parasoft.parabank.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.RequestLoanForm;

/**
 * Controller for applying for a loan
 */
@SuppressWarnings("deprecation")
public class RequestLoanController extends AbstractBankController {

private AccessModeController accessModeController;
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
        ValidationUtils.rejectIfEmpty(errors, "amount", "error.loan.amount.empty");
        ValidationUtils.rejectIfEmpty(errors, "downPayment", "error.down.payment.empty");
        super.onBindAndValidate(request, command, errors);
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
    	 LoanResponse loanResponse = null;
        RequestLoanForm requestLoanForm = (RequestLoanForm)command;
        
        UserSession userSession = (UserSession)WebUtils.getRequiredSessionAttribute(request, "userSession");
        Customer customer = userSession.getCustomer();
      
		String accessMode = null;
		
		if (adminManager != null) {
			accessMode = adminManager.getParameter("accessmode");
		}
        
		if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc") )
        {
        	 loanResponse = accessModeController.doRequestLoan(customer.getId(),
                  requestLoanForm.getAmount(), requestLoanForm.getDownPayment(),
                  requestLoanForm.getFromAccountId());
        }
        
        else{
        	loanResponse = bankManager.requestLoan(customer.getId(),
            requestLoanForm.getAmount(), requestLoanForm.getDownPayment(),
            requestLoanForm.getFromAccountId());
             
        }

        
        
        return new ModelAndView("requestloanConfirm", "loanResponse", loanResponse);
    }
}
