package com.parasoft.parabank.service;

import org.apache.cxf.service.factory.DefaultServiceConfiguration;
import org.apache.cxf.service.model.MessagePartInfo;

/**
 * Defines parameters for XML Schema generation by CXF
 */
public class ParaBankServiceConfiguration extends DefaultServiceConfiguration {

    /*
     * (non-Javadoc)
     * @see org.apache.cxf.service.factory.DefaultServiceConfiguration#getWrapperPartMinOccurs(org.apache.cxf.service.model.MessagePartInfo)
     */
    @Override
    public Long getWrapperPartMinOccurs(MessagePartInfo mpi) {
        // minOccurs should always = 1 so input parameters are not optional
        return 1L;
    }
    
    /*
     * (non-Javadoc)
     * @see org.apache.cxf.service.factory.DefaultServiceConfiguration#isWrapperPartNillable(org.apache.cxf.service.model.MessagePartInfo)
     */
    @Override
    public Boolean isWrapperPartNillable(MessagePartInfo mpi) {
        // input parameters should never be nillable
        return false;
    }
}
