package com.parasoft.parabank.domain.logic;

import java.util.Map;

/**
 * Interface for bank system management
 */
public interface AdminManager {
    
    /**
     * Initialize the data source with the full set of sample data
     * 
     * Run this first after installing ParaBank
     */
    void initializeDB();
    
    /**
     * Reset the data source and populate it with a subset of sample data
     * 
     * Run this before performing a demo 
     */
    void cleanDB();
    
    /**
     * Enable JMS message listener 
     */
    void startupJmsListener();
    
    /**
     * Disable JMS message listener 
     */
    void shutdownJmsListener();
    
    /**
     * Check if JMS message listener is running
     * 
     * @return true if JMS message listener is running
     */
    boolean isJmsListenerRunning();
    
    /**
     * Gets the value of a given configuration parameter
     * 
     * @param name the name of the parameter
     * @return the value of the parameter
     */
    String getParameter(String name);
    
    /**
     * Sets the value of a given configuration parameter
     * 
     * @param name the name of the parameter
     * @param value the value to set
     */
    void setParameter(String name, String value);
    
    /**
     * Gets all configurable application parameters
     *
     * @return map of application parameters
     */
    Map<String, String> getParameters();
}
