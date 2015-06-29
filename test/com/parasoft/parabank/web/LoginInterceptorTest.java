package com.parasoft.parabank.web;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import com.parasoft.parabank.domain.Customer;

import junit.framework.TestCase;

public class LoginInterceptorTest extends TestCase {
    private static final String SERVLET_PATH = "/test.htm";
    private static final String QUERY_STRING = "param=value";
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @Override
    protected void setUp() throws Exception {
        request = new MockHttpServletRequest();
        request.setServletPath(SERVLET_PATH);
        response = new MockHttpServletResponse();
    }
    
    public void testLoginInterceptor() throws Exception {
        LoginInterceptor interceptor = new LoginInterceptor();
        
        ModelAndView mav = assertLoginForm(interceptor);
        assertEquals(SERVLET_PATH, mav.getModel().get("loginForwardAction"));
        
        request.setQueryString(QUERY_STRING);
        mav = assertLoginForm(interceptor);
        assertEquals(SERVLET_PATH + "?" + QUERY_STRING, 
                mav.getModel().get("loginForwardAction"));
        
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        assertLoginForm(interceptor);
        
        session.setAttribute("userSession", new UserSession(new Customer()));
        assertTrue(interceptor.preHandle(request, response, null));
    }
    
    private ModelAndView assertLoginForm(LoginInterceptor interceptor) throws Exception {
        try {
            assertFalse(interceptor.preHandle(request, response, null));
            fail("Did not catch expected ModelAndViewDefiningException");
        } catch (ModelAndViewDefiningException e) {
            ModelAndView mav = e.getModelAndView();
            assertEquals("loginform", mav.getViewName());
            return mav;
        }
        
        return null;
    }
}
