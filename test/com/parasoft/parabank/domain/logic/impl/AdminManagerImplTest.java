package com.parasoft.parabank.domain.logic.impl;

import java.util.Map;

import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.test.util.AbstractAdminOperationsTest;

public class AdminManagerImplTest extends AbstractAdminOperationsTest {
    private static final String TEST_PARAMETER = "loanProcessorThreshold";
    private static final String EXPECTED_VALUE = "20";
    
    private AdminManager adminManager;
    
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public void testInitializeDB() throws Exception {
        assertDBInitialized(new DBInitializer() {
            public void initializeDB() throws Exception {
                adminManager.initializeDB();
            }
        });
    }

    public void testCleanDB() throws Exception {
        assertDBClean(new DBCleaner() {
            public void cleanDB() throws Exception {
                adminManager.cleanDB();
            }
        });
    }
    
    public void testStartupJmsListener() {
        assertJmsStartup(new JmsStartupManager() {
            public void startupJmsListener() {
                adminManager.startupJmsListener();
            }
        });
    }
    
    public void testShutdownJmsListener() {
        assertJmsShutdown(new JmsShutdownManager() {           
            public void shutdownJmsListener() {
                adminManager.shutdownJmsListener();
            }
        });
    }
    
    public void testGetParameter() {
        assertEquals(EXPECTED_VALUE, adminManager.getParameter(TEST_PARAMETER));
        assertNull(adminManager.getParameter("unknown"));
    }
    
    public void testSetParameter() {
        final String newValue = "30";
        
        assertEquals(EXPECTED_VALUE, adminManager.getParameter(TEST_PARAMETER));
        adminManager.setParameter("loanProcessorThreshold", newValue);
        assertEquals(newValue, adminManager.getParameter(TEST_PARAMETER));
    }
    
    public void testGetParameters() {
        Map<String, String> parameters = adminManager.getParameters();
        assertNotNull(parameters);
        assertTrue(parameters.size() > 0);
    }
}
