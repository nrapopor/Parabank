package com.parasoft.parabank.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.util.Util;
import com.parasoft.parabank.web.ViewUtil;

/**
 * Controller for starting/stopping JMS listener
 */
public class JmsListenerController implements Controller {
	
    private static final Log log = LogFactory.getLog(JmsListenerController.class);
    
    private AdminManager adminManager;

	public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String sShutdown = request.getParameter("shutdown");
        
        if (Util.isEmpty(sShutdown)) {
            log.warn("Empty shutdown parameter");
            return ViewUtil.createErrorView("error.empty.shutdown.parameter");
        }
        
        boolean shutdown = Boolean.parseBoolean(sShutdown);
//        String conntype = request.getSession().getAttribute("ConnType").toString();
        if (shutdown) {
        	
        	
        //	amc.doJmsShutdown(conntype);
        	adminManager.shutdownJmsListener();
			 log.info("Using regular JDBC connection");
        	
        	
//        	 if (conntype.equals("SOAP")) {
//     			URL url = new URL(
//     					"http://localhost:8080/parabank/services/ParaBank?wsdl");
//     			QName qname = new QName("http://service.parabank.parasoft.com/",
//     					"ParaBank");
//     			Service service = Service.create(url, qname);
//     			ParaBankService obj = service.getPort(ParaBankService.class);
//     		obj.shutdownJmsListener();
//     			log.info("Using SOAP Web Service: ParaBank");
//     		}
//
//     		else if (conntype.equals("RESTXML")) {
//
//     			URL url1 = new URL(
//     					"http://localhost:8080/parabank/services/bank/shutdownJmsListener");
//     							
//     			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
//     			conn.setRequestMethod("POST");
//     			conn.setRequestProperty("Accept", "application/xml");
//     			//JAXBContext jc = JAXBContext.newInstance(Customer.class);
//     			InputStream xml = conn.getInputStream();
//     			//customer = (Customer) jc.createUnmarshaller().unmarshal(xml);
//
//     			conn.disconnect();
//
//     			log.info("Using REST xml Web Service: Bank");
//     		} else if (conntype.equals("RESTJSON")) {
//
//     			URL url1 = new URL(
//     					"http://localhost:8080/parabank/services/bank/shutdownJmsListener");
//     						
//     			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
//     			conn.setRequestMethod("POST");
//     			conn.setRequestProperty("Accept", "application/json");
//
//     			BufferedReader br = new BufferedReader(new InputStreamReader(
//     					(conn.getInputStream())));
//
//     			String output;
//     			
//     			while ((output = br.readLine()) != null) {
////     				customer = new Gson().fromJson(
////     						output.substring(12, output.length() - 1),
////     						Customer.class);
//     				}
//     			conn.disconnect();
//     			log.info("Using REST json Web Service: Bank");
//     		}
//     		else {
//     				
//
//     			adminManager.shutdownJmsListener();
//     				 log.info("Using regular JDBC connection");
//     			} 
            
        } else {
        	
        //	amc.doJmsStartup(conntype);
        	adminManager.startupJmsListener();
			 log.info("Using regular JDBC connection");
//        	if (conntype.equals("SOAP")) {
//     			URL url = new URL(
//     					"http://localhost:8080/parabank/services/ParaBank?wsdl");
//     			QName qname = new QName("http://service.parabank.parasoft.com/",
//     					"ParaBank");
//     			Service service = Service.create(url, qname);
//     			ParaBankService obj = service.getPort(ParaBankService.class);
//     		obj.startupJmsListener();
//     			log.info("Using SOAP Web Service: ParaBank");
//     		}
//
//     		else if (conntype.equals("RESTXML")) {
//
//     			URL url1 = new URL(
//     					"http://localhost:8080/parabank/services/bank/startupJmsListener");
//     							
//     			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
//     			conn.setRequestMethod("POST");
//     			conn.setRequestProperty("Accept", "application/xml");
//     			//JAXBContext jc = JAXBContext.newInstance(Customer.class);
//     			InputStream xml = conn.getInputStream();
//     			//customer = (Customer) jc.createUnmarshaller().unmarshal(xml);
//
//     			conn.disconnect();
//
//     			log.info("Using REST xml Web Service: Bank");
//     		} else if (conntype.equals("RESTJSON")) {
//
//     			URL url1 = new URL(
//     					"http://localhost:8080/parabank/services/bank/startupJmsListener");
//     						
//     			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
//     			conn.setRequestMethod("POST");
//     			conn.setRequestProperty("Accept", "application/json");
//
//     			BufferedReader br = new BufferedReader(new InputStreamReader(
//     					(conn.getInputStream())));
//
//     			String output;
//     			
//     			while ((output = br.readLine()) != null) {
////     				customer = new Gson().fromJson(
////     						output.substring(12, output.length() - 1),
////     						Customer.class);
//     				}
//     			conn.disconnect();
//     			log.info("Using REST json Web Service: Bank");
//     		}
//     		else {
//     				
//
//     			 adminManager.startupJmsListener();
//     				 log.info("Using regular JDBC connection");
//     			} 
//           
        }
        
        response.sendRedirect("admin.htm");
        return null;
    }
}
