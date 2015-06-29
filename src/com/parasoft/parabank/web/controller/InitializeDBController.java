package com.parasoft.parabank.web.controller;

import com.parasoft.parabank.dao.*;
import com.parasoft.parabank.domain.logic.*;
import com.parasoft.parabank.web.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

/**
 * Controller for creating database tables and populating with sample data 
 */
public class InitializeDBController implements Controller {
    private static final Log log = LogFactory.getLog(InitializeDBController.class);
    
    private AdminManager adminManager;
    private AdminDao bookstoreAdminDao;
    
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public void setBookstoreAdminDao(AdminDao bookstoreAdminDao) {
        this.bookstoreAdminDao = bookstoreAdminDao;
    }
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            adminManager.initializeDB();
            bookstoreAdminDao.initializeDB();
            response.sendRedirect("index.htm");
        } catch (Throwable t) {
            log.fatal(t);
            return ViewUtil.createErrorView("error.could.not.initialize.database");
        }
        return null;
    }
}
