package com.parasoft.parabank.domain.logic.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jms.listener.AbstractJmsListeningContainer;

import com.parasoft.parabank.dao.AdminDao;
import com.parasoft.parabank.domain.logic.AdminManager;

/*
 * Implementation of AdminManager
 */
public class AdminManagerImpl implements AdminManager {
    private static final Log log = LogFactory.getLog(AdminManagerImpl.class);
    
    private AdminDao adminDao;
    private AbstractJmsListeningContainer jmsListener;
    
    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    
    public void setJmsListener(AbstractJmsListeningContainer jmsListener) {
        this.jmsListener = jmsListener;
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.AdminManager#initializeDB()
     */
    public void initializeDB() {
        adminDao.initializeDB();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.AdminManager#cleanDB()
     */
    public void cleanDB() {
        adminDao.cleanDB();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.AdminManager#startupJmsListener()
     */
    public void startupJmsListener() {
        jmsListener.start();
        jmsListener.initialize();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.AdminManager#shutdownJmsListener()
     */
    public void shutdownJmsListener() {
        jmsListener.stop();
        jmsListener.shutdown();
    }
    
    public boolean isJmsListenerRunning() {
        return jmsListener.isRunning();
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.AdminManager#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        try {
            return adminDao.getParameter(name);
        } catch (DataAccessException e) {
            log.error("Could not retrieve parameter with name: " + name);
            return null;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.AdminManager#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value) {
        adminDao.setParameter(name, value);
    }
    
    /*
     * (non-Javadoc)
     * @see com.parasoft.parabank.domain.logic.AdminManager#getParameters()
     */
    public Map<String, String> getParameters() {
        return adminDao.getParameters();
    }
}
