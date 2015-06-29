package com.parasoft.parabank.web.controller;

import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.messaging.MockJmsListeningContainer;

public class JmsListenerControllerTest
extends AbstractControllerTest<JmsListenerController> {
    private AdminManager adminManager;
    private MockJmsListeningContainer jmsListener;
    
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public void setJmsListener(MockJmsListeningContainer jmsListener) {
        this.jmsListener = jmsListener;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller.setAdminManager(adminManager);
    }
    
    public void testHandleBadRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(request, response);
        assertEquals("error", mav.getViewName());
    }
    
    public void testHandleStartup() throws Exception {
        jmsListener.setListenerRunning(false);
        jmsListener.setListenerInitialized(false);
        request.setParameter("shutdown", "false");
        ModelAndView mav = controller.handleRequest(request, response);
        assertNull(mav);
        assertEquals("admin.htm", response.getRedirectedUrl());
        assertTrue(jmsListener.isListenerRunning());
        assertTrue(jmsListener.isListenerInitialized());
    }
    
    public void testHandleShutdown() throws Exception {
        jmsListener.setListenerRunning(true);
        jmsListener.setListenerInitialized(true);
        request.setParameter("shutdown", "true");
        ModelAndView mav = controller.handleRequest(request, response);
        assertNull(mav);
        assertEquals("admin.htm", response.getRedirectedUrl());
        assertFalse(jmsListener.isListenerRunning());
        assertFalse(jmsListener.isListenerInitialized());
    }
}
