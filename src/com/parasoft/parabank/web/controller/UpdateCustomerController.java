package com.parasoft.parabank.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.service.ParaBankServiceException;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.form.CustomerForm;

/**
 * Controller for updating customer information
 */
@SuppressWarnings("deprecation")
public class UpdateCustomerController extends AbstractValidatingBankController {
	
	private AccessModeController accessModeController;
	private AdminManager adminManager;


	public void setAdminManager(AdminManager adminManager) {
	    this.adminManager = adminManager;
	}
		
		
	public void setAccessModeController(AccessModeController accessModeController) {
		this.accessModeController = accessModeController;
	}
	
	
    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
    
    	CustomerForm cf = null;
        UserSession userSession = (UserSession)WebUtils.getRequiredSessionAttribute(request, "userSession");        
        cf = new CustomerForm(bankManager.getCustomer(userSession.getCustomer().getId()));
        return cf;
    }
    
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        CustomerForm customerForm = (CustomerForm)command;
        Customer customer = customerForm.getCustomer();
        
        try {
            errors.pushNestedPath("customer");
            getValidator().validate(customer, errors);
        } finally {
            errors.popNestedPath();
        }
    }
    
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
    						Object command, BindException errors) throws Exception {
    	
        CustomerForm customerForm = (CustomerForm)command;
        
    	String accessMode = null;
    			
    	if (adminManager!= null) {
    		accessMode = adminManager.getParameter("accessmode");
    	}
        
        
        if (accessMode != null && !accessMode.equalsIgnoreCase("jdbc"))
        {
        	accessModeController.updateCustomer(customerForm.getCustomer());
        	UserSession userSession = new UserSession(getCustomer(customerForm.getCustomer().getId()));
            request.getSession().setAttribute("userSession", userSession);
        }
        
        else
        {
        	bankManager.updateCustomer(customerForm.getCustomer());
        	UserSession userSession = new UserSession(bankManager.getCustomer(customerForm.getCustomer().getId()));
            request.getSession().setAttribute("userSession", userSession);
        }
        
        return new ModelAndView("updateprofileConfirm", "customer", customerForm.getCustomer());
    }
    
    
    public Customer getCustomer(int custId) throws ParaBankServiceException, IOException, JAXBException{
    	
    	Customer cu;
    	
    	cu = accessModeController.doGetCustomer(custId);
    	
    	return cu;
    	
    }
}
