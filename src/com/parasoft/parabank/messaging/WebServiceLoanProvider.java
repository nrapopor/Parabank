package com.parasoft.parabank.messaging;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.management.*;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.parasoft.parabank.domain.LoanRequest;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.domain.logic.AdminParameters;
import com.parasoft.parabank.domain.logic.LoanProvider;
import com.parasoft.parabank.service.LoanProcessorService;
import com.parasoft.parabank.service.ParaBankServiceException;
import com.parasoft.parabank.util.Util;

/**
 * Message client for generating and sending loan requests over SOAP
 */
public class WebServiceLoanProvider implements LoanProvider {
    private static final Log log = LogFactory.getLog(WebServiceLoanProvider.class);
    private static final String NL_LOCALHOST = "localhost";
    
    private AdminManager adminManager;
    private String wsdlUrl;
    
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }
    
    public LoanResponse requestLoan(LoanRequest loanRequest) {
        try {
            // If wsdlUrl is localhost, replace the port in the WSDL with the
            // runtime HTTP port
            String[] wsdlUrlSplit = wsdlUrl.split("/");
            if (wsdlUrlSplit.length >= 3
                && !Util.isEmpty(wsdlUrlSplit[2])
                && wsdlUrlSplit[2].startsWith(NL_LOCALHOST)) { 
                int currentPort = getPort();
                if (currentPort > 0) {
                    wsdlUrl = wsdlUrl.replaceFirst(wsdlUrlSplit[2], 
                        NL_LOCALHOST + ":" + currentPort);
                }
            }
            URL wsdlURL = new URL(wsdlUrl);
            QName serviceName = new QName(LoanProcessorService.TNS, "LoanProcessorServiceImplService"); // parasoft-suppress CUSTOM.SLR "Class name"
            Service service = Service.create(wsdlURL, serviceName);
            LoanProcessorService client = service.getPort(LoanProcessorService.class);
            
            String endpoint = adminManager.getParameter(AdminParameters.ENDPOINT);
            if (!Util.isEmpty(endpoint) && client instanceof BindingProvider) {
                ((BindingProvider)client).getRequestContext().put(
                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  endpoint);
            }
            
            return client.requestLoan(loanRequest);
        } catch (MalformedURLException e) {
            log.error(e);
        } catch (ParaBankServiceException e) {
            log.error(e);
        }
        return null;
    }
    
    /**
     * Retrieve the HTTP port currently in use by Tomcat. Returns the default port
     * if any errors occur during retrieval.
     */
    private static int getPort() {
        try {
            ArrayList<?> mBeanServers = MBeanServerFactory.findMBeanServer(null);
            if (mBeanServers != null && mBeanServers.size() > 0 
                && mBeanServers.get(0) instanceof MBeanServer) {
                MBeanServer mBeanServer = (MBeanServer) mBeanServers.get(0);
                ObjectName name = new ObjectName("Catalina", "type", "Server"); // parasoft-suppress CUSTOM.SLR "Class name"
                Object server = mBeanServer.getAttribute(name, "managedResource"); // parasoft-suppress CUSTOM.SLR "Class name"
                if (server != null) {
                    Method findServices = server.getClass().getMethod("findServices"); // parasoft-suppress CUSTOM.SLR "Method name"
                    Object[] services = (Object[]) findServices.invoke(server);
                    for (Object service : services) {
                        Method findConnectors = service.getClass().getMethod("findConnectors"); // parasoft-suppress CUSTOM.SLR "Method name"
                        Object[] connectors = (Object[]) findConnectors.invoke(service);
                        for (Object connector : connectors) {
                            Method getProtocolHandler = connector.getClass().getMethod("getProtocolHandler"); // parasoft-suppress CUSTOM.SLR "Method name"
                            Object protocolHandler = getProtocolHandler.invoke(connector);
                            String handlerType = protocolHandler.getClass().getName();
                            if (handlerType.endsWith("Http11Protocol") // parasoft-suppress CUSTOM.SLR "Class name"
                                || handlerType.endsWith("Http11AprProtocol") // parasoft-suppress CUSTOM.SLR "Class name"
                                || handlerType.endsWith("Http11NioProtocol")) { // parasoft-suppress CUSTOM.SLR "Class name"
                                Method getPort = connector.getClass().getMethod("getPort"); // parasoft-suppress CUSTOM.SLR "Method name"
                                Object port = getPort.invoke(connector);
                                if (port instanceof Integer) {
                                    return (Integer) port;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // We're not in Tomcat or something unexpected happened, so we 
            // assume the default port
        }
        return -1;
    }
}
