package com.parasoft.parabank.web.controller;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.web.UserSession;

public class LogoutControllerTest
extends AbstractControllerTest<LogoutController> {
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        
        MockHttpSession session = new MockHttpSession();
        Customer customer = new Customer();
        customer.setId(1);
        session.setAttribute("userSession", new UserSession(customer));
        request.setSession(session);
    }
    
    public void testHandleRequest() throws Exception {
        UserSession session = (UserSession)request.getSession().getAttribute("userSession");
        assertNotNull(session);
        
        ModelAndView mav = controller.handleRequest(request, response);
        
        assertNull(mav);
        assertEquals("index.htm", response.getRedirectedUrl());
        session = (UserSession)request.getSession().getAttribute("userSession");
        assertNull(session);
    }
}
