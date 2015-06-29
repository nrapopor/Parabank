package com.parasoft.parabank.service;

import junit.framework.TestCase;

public class ParaBankServiceConfigurationTest extends TestCase {
    private ParaBankServiceConfiguration configuration;
    
    @Override
    protected void setUp() throws Exception {
        configuration = new ParaBankServiceConfiguration();
    }
    
    public void testGetWrapperPartMinOccurs() {
        assertEquals(new Long(1), configuration.getWrapperPartMinOccurs(null));
    }
    
    public void testIsWrapperPartNillable() {
        assertEquals(Boolean.FALSE, configuration.isWrapperPartNillable(null));
    }
}
