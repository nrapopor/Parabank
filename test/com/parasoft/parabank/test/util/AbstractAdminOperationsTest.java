package com.parasoft.parabank.test.util;

import com.parasoft.parabank.messaging.MockJmsListeningContainer;

@SuppressWarnings("deprecation")
public abstract class AbstractAdminOperationsTest extends AbstractParaBankDataSourceTest {
    private MockJmsListeningContainer jmsListener;
    
    public void setJmsListener(MockJmsListeningContainer jmsListener) {
        this.jmsListener = jmsListener;
    }
    
    protected void assertDBClean(DBCleaner dbCleaner) throws Exception {
        dbCleaner.cleanDB();
        assertEquals(1, getJdbcTemplate().queryForInt("SELECT COUNT(id) FROM Customer"));
        assertEquals(1, getJdbcTemplate().queryForInt("SELECT COUNT(id) FROM Account"));
        assertEquals(0, getJdbcTemplate().queryForInt("SELECT COUNT(id) FROM Transaction"));
    }
    
    protected void assertDBInitialized(DBInitializer dbInitializer) throws Exception {
        dbInitializer.initializeDB();
        assertEquals(2, getJdbcTemplate().queryForInt("SELECT COUNT(id) FROM Customer"));
        assertEquals(12, getJdbcTemplate().queryForInt("SELECT COUNT(id) FROM Account"));
        assertEquals(21, getJdbcTemplate().queryForInt("SELECT COUNT(id) FROM Transaction"));
    }
    
    protected void assertJmsStartup(JmsStartupManager jmsStartupManager) {
        jmsListener.setListenerRunning(false);
        jmsListener.setListenerInitialized(false);
        jmsStartupManager.startupJmsListener();
        assertTrue(jmsListener.isListenerRunning());
        assertTrue(jmsListener.isListenerInitialized());
    }
    
    protected void assertJmsShutdown(JmsShutdownManager jmsShutdownManager) {
        jmsListener.setListenerRunning(true);
        jmsListener.setListenerInitialized(true);
        jmsShutdownManager.shutdownJmsListener();
        assertFalse(jmsListener.isListenerRunning());
        assertFalse(jmsListener.isListenerInitialized());
    }
    
    protected interface DBCleaner {
        void cleanDB() throws Exception;
    }
    
    protected interface DBInitializer {
        void initializeDB() throws Exception;
    }
    
    protected interface JmsStartupManager {
        void startupJmsListener();
    }
    
    protected interface JmsShutdownManager {
        void shutdownJmsListener();
    }
}
