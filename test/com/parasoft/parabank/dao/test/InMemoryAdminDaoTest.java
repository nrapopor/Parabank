package com.parasoft.parabank.dao.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.parasoft.parabank.dao.AdminDao;
import com.parasoft.parabank.dao.InMemoryAdminDao;

public class InMemoryAdminDaoTest extends TestCase {
    private static final String NAME1 = "name1";   
    private static final String VALUE1 = "value1";
    private static final String NAME2 = "name2";   
    private static final String VALUE2 = "value2";
    
    private AdminDao adminDao;
    
    @Override
    protected void setUp() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        
        parameters.put(NAME1, VALUE1);
        parameters.put(NAME2, VALUE2);
        
        adminDao = new InMemoryAdminDao(parameters);
    }
    
    public void testGetParameter() {
        assertEquals(VALUE1, adminDao.getParameter(NAME1));
        assertEquals(VALUE2, adminDao.getParameter(NAME2));
                
        assertNull(adminDao.getParameter(null));
        assertNull(adminDao.getParameter(""));
        assertNull(adminDao.getParameter("unknown"));
    }
    
    public void testSetParameter() {
        assertEquals(VALUE1, adminDao.getParameter(NAME1));
        adminDao.setParameter(NAME1, VALUE2);
        assertEquals(VALUE2, adminDao.getParameter(NAME1));
    }
    
    public void testGetParameters() {
        assertNotNull(adminDao.getParameters());
        assertEquals(2, adminDao.getParameters().size());
    }
    
    public void testNullMethods() {
        adminDao.initializeDB();
        adminDao.cleanDB();
    }
}
