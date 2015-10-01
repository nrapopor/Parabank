package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.util.Util;
import com.parasoft.parabank.web.ViewUtil;

/**
 * Controller for manipulating database entries
 */
public class DatabaseController implements Controller {
    private static final Log log = LogFactory.getLog(DatabaseController.class);
    
    private AdminManager adminManager;
    
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    
    /**
     *  @param request
     *  @param response
     *  @throws java.lang.Exception
     *  @return
     * 
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String action = request.getParameter("action");
        
        
        if (Util.isEmpty(action)) {
            log.warn("Empty action parameter");
            return ViewUtil.createErrorView("error.empty.action.parameter");
        }
        
        // String conntype = request.getSession().getAttribute("ConnType").toString();
	
        // WebService code starts here
        // System.out.println("          " + conntype);
        if ("INIT".equals(action)) {
        	
        	adminManager.initializeDB();
       	 log.info("Using regular JDBC connection");
       
        	
           
        } else if ("CLEAN".equals(action)) {
        	adminManager.cleanDB();
			 log.info("Using regular JDBC connection");
       
             
        } else {
            log.warn("Unrecognized database action: " + action);
            return ViewUtil.createErrorView("error.invalid.action.parameter");
        }
        
        response.sendRedirect("admin.htm");
        return null;
    }
}

