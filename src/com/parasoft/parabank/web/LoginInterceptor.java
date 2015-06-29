package com.parasoft.parabank.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

/**
 * Spring MVC Interceptor that tests if a user is logged in (i.e. session 
 * object exists) before granting access to protected pages
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Log log = LogFactory.getLog(LoginInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        UserSession userSession = (UserSession)WebUtils.getSessionAttribute(request, "userSession");
        if (userSession == null) {
            String url = request.getServletPath();
            String query = request.getQueryString();
            ModelAndView modelAndView = new ModelAndView("loginform");
            if (query != null) {
                log.warn("User is not logged in and attempting to access protected page: " + url + "?" + query);
                modelAndView.addObject("loginForwardAction", url + "?" + query);
            } else {
                log.warn("User is not logged in and attempting to access protected page: " + url);
                modelAndView.addObject("loginForwardAction", url);
            }
            throw new ModelAndViewDefiningException(modelAndView);
        }
        return super.preHandle(request, response, handler);
    }
}
