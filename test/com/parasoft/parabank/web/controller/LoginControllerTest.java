package com.parasoft.parabank.web.controller;

import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.web.UserSession;


public class LoginControllerTest
extends AbstractBankControllerTest<LoginController> {


	public void onSetUp() throws Exception {
		super.onSetUp();
		controller.setAccessModeController(amc);
	}

    public void testHandleForward() throws Exception {
        assertGetRequest("overview.htm");     
    }
    
    public void testHandleRedirect() throws Exception {
        request.setParameter("forwardAction", "page.htm");
        assertGetRequest("page.htm");
    }
    
    public void assertGetRequest(String url) throws Exception {
        request.setParameter("username", "john");
        request.setParameter("password", "demo");
        ModelAndView mav = controller.handleRequest(request, response);
        assertNull(mav);
        assertEquals(url, response.getRedirectedUrl());
        UserSession session = (UserSession)request.getSession().getAttribute("userSession");
        assertNotNull(session);
        assertEquals(12212, session.getCustomer().getId());
    }
    
    public void testHandleBadGetRequest() throws Exception {
        assertError("error.empty.username.or.password");
        
        request.setParameter("username", "user");
        assertError("error.empty.username.or.password");
        
        request.setParameter("username", "");
        request.setParameter("password", "pass");
        assertError("error.empty.username.or.password");
        
        request.setParameter("username", "user");
        request.setParameter("password", "pass");
        assertError("error.invalid.username.or.password");
    }
    
    public void assertError(String message) throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        assertEquals("error", mav.getViewName());
        assertEquals(message, getModelValue(mav, "message"));
    }
}
