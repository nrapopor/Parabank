package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller for logging out a customer
 */
public class LogoutController implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	request.getSession().removeAttribute("userSession");
        request.getSession().invalidate();
        request.getSession().setAttribute("ConnType", request.getSession().getAttribute("ConnType"));
        response.sendRedirect("index.htm");
        return null;
    }
}
