package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import com.parasoft.parabank.web.ViewUtil;

public class ChangeConnectionController extends AbstractBankController {
	// private static final Log log = LogFactory.getLog(LoginController.class);

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// String username = request.getParameter("username");
		// String password = request.getParameter("password");
		String selected = request.getParameter("choice");
		// System.out.println();
		// System.out.println(selected);
		// System.out.println();
		//
		// This code is to set the default connection type as JDBC
		if (request.getSession().getAttribute("ConnType") == null)
			request.getSession().setAttribute("ConnType", "JDBC");
		if (selected != null)
			request.getSession().setAttribute("ConnType", selected);
		// System.out.println( request.getSession().getAttribute("ConnType"));
		response.sendRedirect("admin.htm");
		return ViewUtil.createErrorView("error.invalid.username.or.password");
	}

}
