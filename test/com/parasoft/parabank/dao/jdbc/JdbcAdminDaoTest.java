package com.parasoft.parabank.dao.jdbc;

import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.parasoft.parabank.dao.AdminDao;
import com.parasoft.parabank.test.util.AbstractAdminOperationsTest;

public class JdbcAdminDaoTest extends AbstractAdminOperationsTest {
    private static final String TEST_PARAMETER = "loanProcessorThreshold";
    private static final String EXPECTED_VALUE = "20";
    
    private AdminDao adminDao;
    
    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    
    public void testCleanDB() throws Exception {
        assertDBClean(new DBCleaner() {
            public void cleanDB() {
                adminDao.cleanDB();
            }
        });
    }
    
    public void testInitalizeDB() throws Exception {
        assertDBInitialized(new DBInitializer() {
            public void initializeDB() {
                adminDao.initializeDB();
            }
        });
    }
    
    public void testGetParameter() {
        assertEquals(EXPECTED_VALUE, adminDao.getParameter(TEST_PARAMETER));
        
        try {
            adminDao.getParameter(null);
            adminDao.getParameter("");
            adminDao.getParameter("unknown");
            fail("did not throw expected DataAccessException");
        } catch (DataAccessException e) { }
    }
    
    public void testSetParameter() {
        final String newValue = "30";
        
        assertEquals(EXPECTED_VALUE, adminDao.getParameter(TEST_PARAMETER));
        adminDao.setParameter("loanProcessorThreshold", newValue);
        assertEquals(newValue, adminDao.getParameter(TEST_PARAMETER));
    }
    
    public void testGetParameters() {
        Map<String, String> parameters = adminDao.getParameters();
        assertNotNull(parameters);
        assertTrue(parameters.size() > 0);
    }
}
