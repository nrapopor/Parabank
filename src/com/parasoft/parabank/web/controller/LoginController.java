package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.util.AccessModeController;
import com.parasoft.parabank.web.UserSession;
import com.parasoft.parabank.web.ViewUtil;


/**
 * Controller for looking up and logging in customer
 */
public class LoginController extends AbstractBankController {
	private static final Log log = LogFactory.getLog(LoginController.class);

	
	private AccessModeController accessModeController ;
	
	
	public void setAccessModeController(AccessModeController accessModeController) {
		this.accessModeController = accessModeController;
	}



	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Customer customer = null;		
		
		if (username == null || username.length() <= 0 || password == null
				|| password.length() <= 0) {
			log.warn("Empty username and/or password used for login");
			return ViewUtil.createErrorView("error.empty.username.or.password");
		}
		
		// login function is handled by the appropriate access mode handler
		customer = accessModeController.login(username,password);
		
			if (customer == null) {
			log.warn("Invalid login attempt with username = " + username
					+ " and password = " + password);
			return ViewUtil
					.createErrorView("error.invalid.username.or.password");
		}

		UserSession userSession = new UserSession(customer);
		request.getSession().setAttribute("userSession", userSession);
		String forwardAction = request.getParameter("forwardAction");
		
		if (forwardAction != null) {
			log.info("Forwarding response to original request url: "
					+ forwardAction);
			response.sendRedirect(forwardAction);
			return null;
		} else {
			response.sendRedirect("overview.htm");
			return null;
		}
	}
}
