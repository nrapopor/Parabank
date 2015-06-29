package com.parasoft.parabank.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.WebUtils;

import com.parasoft.parabank.util.Util;

/**
 * Controller for home page
 */
public class ErrorController implements Controller {
    private static final Log log = LogFactory.getLog(ErrorController.class);
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        Object obj = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
        if (Util.equals(request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE),
                new Integer(404))) {
            log.warn("Page not found: " + request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE));
            model.put("message", "error.not.found");
            model.put("parameters", new Object[] { 
                    request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) });
        } else if (obj instanceof Exception && 
                ((Exception)obj).getCause() instanceof Fault) {
            response.setStatus(400);
            response.setContentType("text/plain");
            response.getWriter().write(((Exception)obj).getCause().getLocalizedMessage());
            return null;
        } else {
            model.put("message", "error.internal");
        }
        return new ModelAndView("error", "model", model);
    }
}
