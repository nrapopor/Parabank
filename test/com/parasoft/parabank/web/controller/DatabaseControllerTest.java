package com.parasoft.parabank.web.controller;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.test.util.AbstractAdminOperationsTest;

@SuppressWarnings("deprecation")
public class DatabaseControllerTest extends AbstractAdminOperationsTest {
    private DatabaseController controller;
    protected AdminManager adminManager;
    
    public final void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        controller = new DatabaseController();
        controller.setAdminManager(adminManager);
    }
    
    public void testHandleRequest() throws Exception {
        assertDBInitialized(new DBInitializer() {
            public void initializeDB() throws Exception {
                MockHttpServletRequest request = new MockHttpServletRequest();
                request.setParameter("action", "INIT");
                controller.handleRequest(request, new MockHttpServletResponse());                
            }
        });
        
        assertDBClean(new DBCleaner() {
            public void cleanDB() throws Exception {
                MockHttpServletRequest request = new MockHttpServletRequest();
                request.setParameter("action", "CLEAN");
                controller.handleRequest(request, new MockHttpServletResponse());                
            }
        });
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelAndView mav = controller.handleRequest(request, new MockHttpServletResponse());
        assertEquals("error", mav.getViewName());
        
        request.setParameter("action", "unknown");
        mav = controller.handleRequest(request, new MockHttpServletResponse());
        assertEquals("error", mav.getViewName());
    }
}
