package com.parasoft.parabank.web.controller;

import org.springframework.web.servlet.ModelAndView;


public class PortfolioControllerTest 
    extends AbstractControllerTest<PortfolioController>{
        
    public void testHandleRequest() throws Exception {
        ModelAndView mav = controller.handleRequest(null, null);      
        assertEquals("portfolio", mav.getViewName());
        assertNotNull(mav.getModel());
    }

}
