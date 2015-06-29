package com.parasoft.parabank.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11AprProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.coyote.http11.Http11Protocol;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Address;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.Transaction;
import com.parasoft.parabank.domain.TransactionCriteria;
import com.parasoft.parabank.domain.logic.AdminManager;
import com.parasoft.parabank.domain.logic.BankManager;
import com.parasoft.parabank.service.ParaBankService;
import com.parasoft.parabank.service.ParaBankServiceException;



//This class delegates all the function calls according to the access mode  Viz. SOAP, REST XML , REST JSON, JDBC(Default)
/**
 *  @author deepakv
 * 
 *  
 * 
 */
public class AccessModeController {
	

	private Customer customer = null;
	
	private static final Log LOG = LogFactory.getLog(AccessModeController.class);
	
	private static final String GET = "GET";
	private static final String POST = "POST";
	
	private static final int DEFAULT_CATALINA_PORT = 8080;
	private final int CATALINA_PORT;
	
	private AdminManager adminManager ;
	private BankManager bankManager;
	    
	public AccessModeController() {
    	this.CATALINA_PORT = getCatalinaPort();
    	LOG.info("Getting Tomcat HTTP port = " + CATALINA_PORT);
	}
	
	private static int getCatalinaPort() {
		try {
	    	MBeanServer mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
	        ObjectName name = new ObjectName("Catalina", "type", "Server");
	        Server server = (Server) mBeanServer.getAttribute(name, "managedResource");
	        for (org.apache.catalina.Service service : server.findServices()) {
	        	for (Connector connector : service.findConnectors()) {
	        		ProtocolHandler protocolHandler = connector.getProtocolHandler();
	                if (protocolHandler instanceof Http11Protocol
	                		|| protocolHandler instanceof Http11AprProtocol
	                		|| protocolHandler instanceof Http11NioProtocol) {
	                	return connector.getPort();
	                }
	        	}
	        }
    	} catch (Exception e) {
    		LOG.error(e.getMessage(), e);
    	}
		
		return DEFAULT_CATALINA_PORT;
	}
	
	public void setBankManager(BankManager bankManager) {
		this.bankManager = bankManager;
	}


	public void setAdminManager(AdminManager adminManager) {
	    this.adminManager = adminManager;
	}
		

	/**
	 *  @throws java.io.IOException
	 * 
	 */
	public void doDBinit() throws IOException
	{
		String accessMode = adminManager.getParameter("accessmode");	
		
		if (accessMode.equalsIgnoreCase("SOAP"))
		{	
			URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/",
					"ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			parabankService.initializeDB();
			LOG.info("Using SOAP Web Service: ParaBank");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML"))
		{
			URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/bank/initializeDB");
							
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/xml");			
			//InputStream xml = conn.getInputStream();
			conn.disconnect();

			LOG.info("Using REST xml Web Service: Bank");
		}
		else if (accessMode.equalsIgnoreCase("RESTJSON")) 
		{
			URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/bank/initializeDB");
						
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");

			//BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			LOG.info("Using REST json Web Service: Bank");
		}
		else
		{
			adminManager.initializeDB();
			LOG.info("Using regular JDBC connection");
		}	
	}
		

	/**
	 *  @throws java.io.IOException
	 * 
	 */
	public void doDBClean() throws IOException
	{
		String accessMode = adminManager.getParameter("accessmode");
		
		if (accessMode.equalsIgnoreCase("SOAP"))
		{
			URL url = new URL(
 					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
 			QName qname = new QName("http://service.parabank.parasoft.com/",
 					"ParaBank");
 			Service service = Service.create(url, qname);
 			ParaBankService parabankService = service.getPort(ParaBankService.class);
 			parabankService.cleanDB();
 			LOG.info("Using SOAP Web Service: ParaBank");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML"))
		{
			URL url = new URL("http://localhost:" + CATALINA_PORT + "/parabank/services/bank/cleanDB");
							
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/xml");
			//InputStream xml = conn.getInputStream();
			conn.disconnect();
			LOG.info("Using REST xml Web Service: Bank");

		}
		else if (accessMode.equalsIgnoreCase("RESTJSON")) 
		{
			URL url = new URL("http://localhost:" + CATALINA_PORT + "/parabank/services/bank/cleanDB");
 			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
 			conn.setRequestMethod("POST");
 			conn.setRequestProperty("Accept", "application/json");
 			//BufferedReader br = new BufferedReader(new InputStreamReader(
 				//	(conn.getInputStream())));
 			conn.disconnect();
 			LOG.info("Using REST json Web Service: Bank");			
		}
		else
		{
 			adminManager.cleanDB();
 			LOG.info("Using regular JDBC connection");
		}
	}
	
	
	/**
	 *  @param username
	 *  @param password
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws java.io.IOException
	 *  @throws javax.xml.bind.JAXBException
	 *  @return
	 * 
	 */
	public Customer login (String username,String password) throws ParaBankServiceException, IOException, JAXBException
	{
		String accessMode = null, soapEndpoint = null, restEndpoint = null;
		
		if (adminManager != null) {
			accessMode = adminManager.getParameter("accessmode");
			soapEndpoint = adminManager.getParameter("soap_endpoint");
			restEndpoint = adminManager.getParameter("rest_endpoint");
		}
				
        if (Util.isEmpty(restEndpoint)) {
            restEndpoint = getDefaultRestEndpoint();
        }
        
		if (accessMode != null && accessMode.equalsIgnoreCase("SOAP")) {		              

			URL url = new URL("http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/","ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
            if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
                ((BindingProvider)parabankService).getRequestContext().put(
                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
            }
            
            try {
            	customer = parabankService.login(username, password);
            }
            catch (ParaBankServiceException e) {}
            
			LOG.info("Using SOAP Web Service: ParaBank");
		}

		else if (accessMode != null && accessMode.equalsIgnoreCase("RESTXML")) {
			
			URL url = new URL( restEndpoint + "/login/" + username + "/" + password);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, GET);
		    LOG.info(url);
			
			JAXBContext jc = JAXBContext.newInstance(Customer.class);
			InputStream xml = null;
			
			try {
				xml = conn.getInputStream();
			}
			catch (IOException e) {}
			
			if (xml != null) {
				customer = (Customer) jc.createUnmarshaller().unmarshal(xml);
			}
           
			conn.disconnect();

			LOG.info("Using REST xml Web Service: Bank");
		} else if (accessMode != null && accessMode.equalsIgnoreCase("RESTJSON")) {

			URL url = new URL( restEndpoint + "/login/" + username + "/" + password);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, GET);
			BufferedReader br = null;
				
			try {
				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			}
			catch (IOException e) {}

			String output;

			if (br != null) {
				while ((output = br.readLine()) != null) {
					customer = new Gson().fromJson(
							output.substring(12, output.length() - 1),
							Customer.class);
				}
			}
			
			conn.disconnect();
			LOG.info("Using REST json Web Service: Bank");
		} else {

			customer = bankManager.getCustomer(username, password);
			LOG.warn("Using regular JDBC connection");
			System.out.print("cutomer=" + customer);
		}
		
		return customer;
	}
       
	
	/**
	 *  @param fromAccountId
	 *  @param toAccountId
	 *  @param amount
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws java.io.IOException
	 *  @throws javax.xml.bind.JAXBException
	 * 
	 */
	public void doTransfer(int fromAccountId, int toAccountId, BigDecimal amount  ) throws ParaBankServiceException, IOException, JAXBException
	{
		String accessMode = adminManager.getParameter("accessmode");		
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		LOG.info("endpoint = " + restEndpoint);
		
		if (Util.isEmpty(restEndpoint)) {
			restEndpoint = getDefaultRestEndpoint();
	    }
		
		if (accessMode.equalsIgnoreCase("SOAP")) {		              
		
			URL url = new URL("http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/","ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			        
	        if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
	        	((BindingProvider)parabankService).getRequestContext().put(
	            BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
	        }
	            
	        parabankService.transfer(fromAccountId, toAccountId, amount);
			LOG.info("Using SOAP Web Service");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML")) {
			
			URL url = new URL(restEndpoint + "/transfer?"
							+ "toAccountId=" + toAccountId + "&" + "amount=" + amount 
							+ "&" + "fromAccountId=" + fromAccountId);
		
			LOG.info(url.toExternalForm());
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, POST);
			
			//this is mandatory for the function to work though we do not use response anywhere
			conn.getResponseMessage();
   
			conn.disconnect();
			LOG.info("Using REST xml Web Service");
			
		} else if (accessMode.equalsIgnoreCase("RESTJSON")) {

			URL url = new URL(
					restEndpoint + "/transfer?"
							+ "toAccountId=" + toAccountId+ "&" + "amount=" + amount + "&" + "fromAccountId=" + fromAccountId);
			
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, POST);
			conn.getResponseMessage();
			conn.disconnect();
			LOG.info("Using REST json Web Service");
		}
	}
	
	/**
	 *  @param customer
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws java.io.IOException
	 *  @throws javax.xml.bind.JAXBException
	 *  @return
	 * 
	 */
	public List<Account> doGetAccounts(Customer customer) throws ParaBankServiceException, IOException, JAXBException{
        
		List<Account> accounts= new ArrayList<Account>();
		Accounts acs = new Accounts(); 
       //  Map <String, List<Account>> Accs = new  HashMap<String, List<Account>>();
        String accessMode = adminManager.getParameter("accessmode");		
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		
        if (Util.isEmpty(restEndpoint)) {
            restEndpoint = getDefaultRestEndpoint();
        }
        
		if (accessMode.equalsIgnoreCase("SOAP")) 
		{	
			URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/",
					"ParaBank");
			Service service = Service.create(url, qname);			
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
			if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
				((BindingProvider)parabankService).getRequestContext().put(
	                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
	        }
			 
			accounts = parabankService.getAccounts(customer.getId());
			LOG.info("Using SOAP Web Service: ParaBank");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML")) {
			
			URL url = new URL( restEndpoint + "/customers/" + customer.getId() + "/accounts");
		
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, GET);
		
			JAXBContext jc = JAXBContext.newInstance(Accounts.class);
			Unmarshaller um = jc.createUnmarshaller();
			InputStream inputStream = conn.getInputStream();
							
			acs = (Accounts) um.unmarshal(inputStream);
			
			accounts=acs.getAccs();
			
			conn.disconnect();

			LOG.info("Using REST xml Web Service: Bank");
			
		} else if (accessMode.equalsIgnoreCase("RESTJSON")) {

			URL url = new URL( restEndpoint + "/customers/" + customer.getId() + "/accounts");

			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, GET);
			InputStream inputStream  = conn.getInputStream();
			
			JsonParser parser = new JsonParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String message = reader.readLine();
			JsonElement rootElement = parser.parse(message);
			JsonObject obj = rootElement.getAsJsonObject();
			JsonArray arr = obj.get("account").getAsJsonArray();
			
			for (int i = 0; i < arr.size(); i++) {
				Account acct = Account.readFrom(arr.get(i).getAsJsonObject());
				LOG.info("Account read: " + acct.getId());
				accounts.add(acct);
			}

			conn.disconnect();
			LOG.info("Using REST json Web Service: Bank");
		}
		
		return accounts;
	}
	
	
	/**
	 *  @param accountId
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws javax.xml.bind.JAXBException
	 *  @throws java.io.IOException
	 *  @throws java.text.ParseException
	 *  @return
	 * 
	 */
	public  List<Transaction> doGetTransactions(int accountId) throws ParaBankServiceException, JAXBException, IOException, ParseException
	{
		String accessMode = adminManager.getParameter("accessmode");		
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		
        if (Util.isEmpty(restEndpoint)) {
            restEndpoint = getDefaultRestEndpoint();
        }
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transactions ts = new Transactions();
		 
		if (accessMode.equalsIgnoreCase("SOAP")) 
		{	
			URL url = new URL("http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/", "ParaBank");
			Service service = Service.create(url, qname);			
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
			if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
				((BindingProvider)parabankService).getRequestContext().put(
	                       	BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
	        }
			  
			transactions = parabankService.getTransactions(accountId);
			LOG.info("Using SOAP Web Service: ParaBank");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML")) {
				
			HttpURLConnection conn = getConnection(createGetTransactionsRestUrl(accountId, 
										null, restEndpoint), MediaType.APPLICATION_XML, GET);
				
			JAXBContext jc = JAXBContext.newInstance(Transactions.class);
			Unmarshaller um = jc.createUnmarshaller();
			InputStream xml = conn.getInputStream();
			ts = (Transactions) um.unmarshal(xml);
				
			transactions = ts.getTranss();
				
			conn.disconnect();

			LOG.info("Using REST xml Web Service");
		}
		else if (accessMode.equalsIgnoreCase("RESTJSON")) {

			HttpURLConnection conn = getConnection(createGetTransactionsRestUrl(accountId, 
										null, restEndpoint), MediaType.APPLICATION_JSON, GET);
				
			InputStream inputStream  = conn.getInputStream();
				
			JsonParser parser = new JsonParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String message = reader.readLine();
			JsonElement rootElement = parser.parse(message);
			JsonObject obj = rootElement.getAsJsonObject();
			JsonArray arr = obj.get("transaction").getAsJsonArray();
			
			for (int i = 0; i < arr.size(); i++) {
				Transaction trans = Transaction.readFrom(arr.get(i).getAsJsonObject());
				//Account acct = Account.readFrom(arr.get(i).getAsJsonObject());
				LOG.info("Account read: " + trans.getId());
				transactions.add(trans);
			}				
			conn.disconnect();

			LOG.info("Using REST JSON Web Service");
		}

		return transactions;
	}
	
	
	/**
	 *  @param id
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws java.io.IOException
	 *  @throws javax.xml.bind.JAXBException
	 *  @return
	 * 
	 */
	public Account doGetAccount(int id) throws ParaBankServiceException, IOException, JAXBException	
	{
		Account account = new Account();
		String accessMode = adminManager.getParameter("accessmode");		
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		
        if (Util.isEmpty(restEndpoint)) {
            restEndpoint = getDefaultRestEndpoint();
        }
		
		if (accessMode.equalsIgnoreCase("SOAP"))
		{
			URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/",
					"ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
			if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
				((BindingProvider)parabankService).getRequestContext().put(
	                    BindingProvider.ENDPOINT_ADDRESS_PROPERTY, soapEndpoint);
	        }
			
			account = parabankService.getAccount(id);
			LOG.info("Using SOAP Web Service: ParaBank");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML")) {
			
			URL url = new URL(restEndpoint + "/accounts/" + id);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, GET);
			
			JAXBContext jc = JAXBContext.newInstance(Account.class);
			
			InputStream xml = conn.getInputStream();
			account = (Account) jc.createUnmarshaller().unmarshal(xml);
			
			conn.disconnect();

			LOG.info("Using REST xml Web Service");
		} else if (accessMode.equalsIgnoreCase("RESTJSON")) {

			URL url = new URL(restEndpoint + "/accounts/" + id);
			
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, GET);
			InputStream inputStream = conn.getInputStream();
			
			JsonParser parser = new JsonParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String message = reader.readLine();
			JsonElement rootElement = parser.parse(message);
			JsonObject obj = rootElement.getAsJsonObject();
			JsonObject arr = obj.get("account").getAsJsonObject();
			account = Account.readFrom(arr);
			conn.disconnect();
			LOG.info("Using REST json Web Service");
		}

		return account ;
	}
	
	/**
	 *  @param id
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws javax.xml.bind.JAXBException
	 *  @throws java.io.IOException
	 *  @throws java.text.ParseException
	 *  @return
	 * 
	 */
	public Transaction doGetTransaction(int id) throws ParaBankServiceException, JAXBException, IOException, ParseException
	{
	     Transaction transaction = null;
	     String accessMode = adminManager.getParameter("accessmode");		
		 String soapEndpoint = adminManager.getParameter("soap_endpoint");
		 String restEndpoint = adminManager.getParameter("rest_endpoint");
		 
	     if (Util.isEmpty(restEndpoint)) {
	    	 restEndpoint = getDefaultRestEndpoint();
	     }
		
		 if (accessMode.equalsIgnoreCase("SOAP")) {
				
			URL url = new URL("http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/", "ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
			if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
				((BindingProvider)parabankService).getRequestContext().put(
		        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
		    }
				
			transaction = parabankService.getTransaction(id);
				
				 
			LOG.info("Using SOAP Web Service: ParaBank");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML")) {
				
			URL url = new URL(restEndpoint + "/transactions/" + id);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, GET);
	
			JAXBContext jc = JAXBContext.newInstance(Transaction.class);
				
			InputStream xml = conn.getInputStream();
			transaction = (Transaction) jc.createUnmarshaller().unmarshal(xml);
				
			conn.disconnect();

			LOG.info("Using REST xml Web Service");
		}
		else if (accessMode.equalsIgnoreCase("RESTJSON")) {
				
			URL url = new URL(restEndpoint + "/transactions/" + id);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, GET);
			
			InputStream inputStream  = conn.getInputStream();
				
			JsonParser parser = new JsonParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String message = reader.readLine();
			JsonElement rootElement = parser.parse(message);
				
			LOG.info("class=" + rootElement.getClass());
			JsonObject obj = rootElement.getAsJsonObject();
			JsonObject arr = obj.get("transaction").getAsJsonObject();
			    
		    LOG.info("obj:" + obj); 
		    transaction = Transaction.readFrom(arr);
		    conn.disconnect();

			LOG.info("Using REST JSON Web Service");
		}

		return transaction;
	}
	
	
	/**
	 *  @param custid
	 *  @param amt
	 *  @param dwnpay
	 *  @param frmaccid
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws java.io.IOException
	 *  @throws javax.xml.bind.JAXBException
	 *  @throws java.text.ParseException
	 *  @return
	 * 
	 */
	public LoanResponse doRequestLoan(int custid,BigDecimal amt , BigDecimal dwnpay ,int frmaccid) throws ParaBankServiceException, IOException, JAXBException, ParseException
	{
		
		LoanResponse loanResponse = null;
		
	    String accessMode = adminManager.getParameter("accessmode");		
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		
	    if (Util.isEmpty(restEndpoint)) {
	    	restEndpoint = getDefaultRestEndpoint();
	    }
	        
		if (accessMode.equalsIgnoreCase("SOAP"))
		{
			URL url = new URL("http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/", "ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
			if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
				((BindingProvider)parabankService).getRequestContext().put(
		                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
		    }
				
			loanResponse = parabankService.requestLoan(custid, amt, dwnpay, frmaccid);
				 
			LOG.info("Using SOAP Web Service: ParaBank");
		}
		else if (accessMode.equalsIgnoreCase("RESTXML")) {
				
			URL url = new URL(restEndpoint + "/requestLoan?"
								+ "downPayment=" + dwnpay + "&amount=" +  amt 
								+ "&fromAccountId=" + frmaccid + "&customerId=" + custid);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, POST);
			
			JAXBContext jc = JAXBContext.newInstance(LoanResponse.class);
			Unmarshaller um = jc.createUnmarshaller();
			InputStream xml = conn.getInputStream();
			loanResponse = (LoanResponse) um.unmarshal(xml);

			conn.disconnect();

			LOG.info("Using REST xml Web Service");
		}
		else if (accessMode.equalsIgnoreCase("RESTJSON")) {
				
			URL url = new URL(restEndpoint + "/requestLoan?"
								+ "downPayment=" + dwnpay + "&amount=" +  amt 
								+ "&fromAccountId=" + frmaccid + "&customerId=" + custid);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, POST);
			
			InputStream inputStream  = conn.getInputStream();
				
			JsonParser parser = new JsonParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String message = reader.readLine();
			JsonElement rootElement = parser.parse(message);
				
			LOG.info("class=" + rootElement.getClass());
			JsonObject obj = rootElement.getAsJsonObject();
			JsonObject arr = obj.get("ns2.loanResponse").getAsJsonObject();
			     
		    loanResponse = LoanResponse.readFrom(arr);
		    conn.disconnect();
			LOG.info("Using REST JSON Web Service");
		}

		return loanResponse;
	}

	
	/**
	 *  @param custId
	 *  @throws com.parasoft.parabank.service.ParaBankServiceException
	 *  @throws java.io.IOException
	 *  @throws javax.xml.bind.JAXBException
	 *  @return
	 * 
	 */
	public Customer doGetCustomer (int custId) throws ParaBankServiceException, IOException, JAXBException
	{	
		String accessMode = adminManager.getParameter("accessmode");
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
				
        if (Util.isEmpty(restEndpoint)) {
            restEndpoint = getDefaultRestEndpoint();
        }
        
		if (accessMode.equalsIgnoreCase("SOAP")) {
			
			URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/","ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
            if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
                ((BindingProvider)parabankService).getRequestContext().put(
                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
            }
            
            customer = parabankService.getCustomer(custId);
			LOG.info("Using SOAP Web Service: ParaBank");
		}

		else if (accessMode.equalsIgnoreCase("RESTXML")) {
			
			URL url = new URL( restEndpoint + "/customers/" + custId);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, GET);
			
			JAXBContext jc = JAXBContext.newInstance(Customer.class);
			
			InputStream xml = conn.getInputStream();
			customer = (Customer) jc.createUnmarshaller().unmarshal(xml);
            
			conn.disconnect();

			LOG.info("Using REST xml Web Service: Bank");
			
		} else if (accessMode.equalsIgnoreCase("RESTJSON")) {

			URL url = new URL( restEndpoint + "/customers/" + custId);

			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, GET);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			
			String output;

			while ((output = br.readLine()) != null) {
				customer = new Gson().fromJson(
						output.substring(12, output.length() - 1),
						Customer.class);
			}
			
			conn.disconnect();
			LOG.info("Using REST json Web Service: Bank");
		} 
		
		return customer;
	}
	
	
	public Account createAccount(int customerId, int newAccountType, int fromAccountId) 
								throws ParaBankServiceException, IOException, JAXBException {
		
	    String accessMode = adminManager.getParameter("accessmode");		
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		Account createdAccount = new Account();
		
	    if (Util.isEmpty(restEndpoint)) {
	    	restEndpoint = getDefaultRestEndpoint();
	    }
	    
	    if (accessMode.equalsIgnoreCase("SOAP")) {
			
	    	URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/","ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
            if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
                ((BindingProvider)parabankService).getRequestContext().put(
                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
            }

			LOG.info("Using SOAP Web Service: ParaBank");
			createdAccount = parabankService.createAccount(customerId, newAccountType, fromAccountId);
		}

		else if (accessMode.equalsIgnoreCase("RESTXML")) {
			
			URL url = new URL( restEndpoint + "/createAccount?customerId=" + customerId + "&newAccountType=" 
															+ newAccountType + "&fromAccountId=" + fromAccountId);
			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_XML, POST);
			
			JAXBContext jc = JAXBContext.newInstance(Account.class);
			
			InputStream xml = conn.getInputStream();
			createdAccount = (Account) jc.createUnmarshaller().unmarshal(xml);
            
			conn.disconnect();

			LOG.info("Using REST xml Web Service: Bank");
			
		} else if (accessMode.equalsIgnoreCase("RESTJSON")) {

			URL url = new URL( restEndpoint + "/createAccount?customerId=" + customerId + "&newAccountType=" 
															+ newAccountType + "&fromAccountId=" + fromAccountId);

			HttpURLConnection conn = getConnection(url, MediaType.APPLICATION_JSON, POST);
			InputStream inputStream = conn.getInputStream();

			JsonParser parser = new JsonParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String message = reader.readLine();
			JsonElement rootElement = parser.parse(message);
			JsonObject obj = rootElement.getAsJsonObject();
			JsonObject actObject = obj.get("account").getAsJsonObject();
			createdAccount = Account.readFrom(actObject);
			
			conn.disconnect();
			LOG.info("Using REST json Web Service: Bank");
		} 

	    return createdAccount;
	}


	public List<Transaction> getTransactionsForAccount(Account account,
			TransactionCriteria criteria) throws ParaBankServiceException, IOException, 
															JAXBException, ParseException {
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		if (criteria != null && criteria.getTransactionId() != null) {
			transactions.add(doGetTransaction(criteria.getTransactionId()));
			return transactions;
		}
		
		String accessMode = adminManager.getParameter("accessmode");		
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		
	    if (Util.isEmpty(restEndpoint)) {
	    	restEndpoint = getDefaultRestEndpoint();
	    }
	    
	    Transactions xmlTransactions = new Transactions();
	   
	    if (accessMode.equalsIgnoreCase("SOAP")) {
			
	    	URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/","ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
            if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
                ((BindingProvider) parabankService).getRequestContext().put(
                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
            }

			LOG.info("Using SOAP Web Service: ParaBank");
			transactions = getTransactionsSoap(account, criteria, parabankService);
		}
	    else if (accessMode.equalsIgnoreCase("RESTXML")) {
	    	HttpURLConnection connection = 
	    			getConnection(createGetTransactionsRestUrl(account.getId(), criteria, 
	    								restEndpoint), MediaType.APPLICATION_XML, GET);
	    	JAXBContext jc = JAXBContext.newInstance(Transactions.class);
			Unmarshaller um = jc.createUnmarshaller();
			InputStream xml = connection.getInputStream();
			xmlTransactions = (Transactions) um.unmarshal(xml);
				
			transactions = xmlTransactions.getTranss();
				
			connection.disconnect();

			LOG.info("Using REST xml Web Service");
	    }
	    else if (accessMode.equalsIgnoreCase("RESTJSON")) {
	    	HttpURLConnection connection = 
	    			getConnection(createGetTransactionsRestUrl(account.getId(), criteria, 
	    								restEndpoint), MediaType.APPLICATION_JSON, GET);
	    	InputStream inputStream  = connection.getInputStream();
			
			JsonParser parser = new JsonParser();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String message = reader.readLine();
			JsonElement rootElement = parser.parse(message);
			JsonObject obj = rootElement.getAsJsonObject();
			JsonArray arr = obj.get("transaction").getAsJsonArray();
			
			for (int i = 0; i < arr.size(); i++) {
				Transaction trans = Transaction.readFrom(arr.get(i).getAsJsonObject());
				//Account acct = Account.readFrom(arr.get(i).getAsJsonObject());
				LOG.info("Account read: " + trans.getId());
				transactions.add(trans);
			}	
			
			connection.disconnect();

			LOG.info("Using REST JSON Web Service");
	    }

		return transactions;
	}
	
	
	public void updateCustomer(Customer updatedCustomer) throws ParaBankServiceException, IOException, JAXBException {
		String accessMode = adminManager.getParameter("accessmode");	
		String soapEndpoint = adminManager.getParameter("soap_endpoint");
		String restEndpoint = adminManager.getParameter("rest_endpoint");
		
		if (Util.isEmpty(restEndpoint)) {
	    	restEndpoint = getDefaultRestEndpoint();
	    }
		
		Address address = updatedCustomer.getAddress();
		
		if (accessMode.equalsIgnoreCase("SOAP")) {
			
			URL url = new URL(
					"http://localhost:" + CATALINA_PORT + "/parabank/services/ParaBank?wsdl");
			QName qname = new QName("http://service.parabank.parasoft.com/","ParaBank");
			Service service = Service.create(url, qname);
			ParaBankService parabankService = service.getPort(ParaBankService.class);
			
            if (!Util.isEmpty(soapEndpoint) && parabankService instanceof BindingProvider) {
                ((BindingProvider) parabankService).getRequestContext().put(
                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  soapEndpoint);
            }

			LOG.info("Using SOAP Web Service: ParaBank");
			
			parabankService.updateCustomer(updatedCustomer.getId(), updatedCustomer.getFirstName(), updatedCustomer.getLastName(), 
											address.getStreet(), address.getCity(), address.getState(), 
											address.getZipCode(), updatedCustomer.getPhoneNumber(), updatedCustomer.getSsn(), 
											updatedCustomer.getUsername(), updatedCustomer.getPassword());
		}
	    else if (accessMode.equalsIgnoreCase("RESTXML")) {
	    	URL url = createUpdateCustomerUrl(updatedCustomer, address, restEndpoint);
	    	HttpURLConnection connection = getConnection(url, MediaType.APPLICATION_XML, POST);
	    	connection.getResponseMessage();
			connection.disconnect();
			LOG.info("Using REST xml Web Service: Bank");
	    }
	    else if (accessMode.equalsIgnoreCase("RESTJSON")) {
	    	URL url = createUpdateCustomerUrl(updatedCustomer, address, restEndpoint);
	    	HttpURLConnection connection = getConnection(url, MediaType.APPLICATION_JSON, POST);
	    	connection.getResponseMessage();
			connection.disconnect();
			LOG.info("Using REST JSON Web Service");
	    }
	}
	
	
	private HttpURLConnection getConnection(URL url, String mediaType, String method) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Accept", mediaType);
		return conn;
	}
	
	
	private URL createGetTransactionsRestUrl(int accountId, TransactionCriteria criteria, 
											String restEndpoint) throws MalformedURLException {
		
		String urlString = restEndpoint + "/accounts/" + accountId + "/transactions";
		
		if (criteria != null) {
			if (criteria.getAmount() != null) {
				return new URL(urlString + "/amount/" + criteria.getAmount());
			}
			else if (criteria.getOnDate() != null) {
				return new URL(urlString + "/onDate/" + TransactionCriteria.DATE_FORMATTER.get().
																	format(criteria.getOnDate()));
			}
			else if (criteria.getFromDate() != null && criteria.getToDate() != null) {
				return new URL(urlString + "/fromDate/" 
				+ TransactionCriteria.DATE_FORMATTER.get().format(criteria.getFromDate()) + "/toDate/" 
				+ TransactionCriteria.DATE_FORMATTER.get().format(criteria.getToDate()));
			}
			else if (criteria.getMonth() != null && criteria.getTransactionType() != null) {
				return new URL(urlString + "/month/" + criteria.getMonth() + "/type/" + criteria.getTransactionType());
			}
		}
		
		return new URL(urlString);
	}
	
	
	private URL createUpdateCustomerUrl(Customer customer, Address address, 
											String restEndpoint) throws MalformedURLException, UnsupportedEncodingException {
		return new URL( restEndpoint + "/customers/update/" + customer.getId() 
						+ "/" + URLEncoder.encode(customer.getFirstName(), "UTF-8") + "/" 
						+ URLEncoder.encode(customer.getLastName(), "UTF-8") + "/" 
						+ URLEncoder.encode(address.getStreet(), "UTF-8") + "/" 
						+ URLEncoder.encode(address.getCity(), "UTF-8")  + "/" + address.getState() + "/" 
						+ URLEncoder.encode(address.getZipCode(), "UTF-8") + "/" 
						+ URLEncoder.encode(customer.getPhoneNumber(), "UTF-8") + "/" 
						+ customer.getSsn() + "/" + customer.getUsername() + "/" + customer.getPassword());
	}


	private List<Transaction> getTransactionsSoap(Account account, TransactionCriteria criteria, 
								ParaBankService parabankService) throws ParaBankServiceException, 
														JAXBException, IOException, ParseException {
		if (criteria != null) {
			if (criteria.getAmount() != null) {
				return parabankService.getTransactionsByAmount(account.getId(), criteria.getAmount());
			}
			else if (criteria.getOnDate() != null) {
				return parabankService.getTransactionsOnDate(account.getId(), 
								TransactionCriteria.DATE_FORMATTER.get().format(criteria.getOnDate()));
			}
			else if (criteria.getFromDate() != null && criteria.getToDate() != null) {
				return parabankService.getTransactionsByToFromDate(account.getId(), 
						TransactionCriteria.DATE_FORMATTER.get().format(criteria.getFromDate()), 
						TransactionCriteria.DATE_FORMATTER.get().format(criteria.getToDate()));
			}
			else if (criteria.getMonth() != null && criteria.getTransactionType() != null) {
				return parabankService.getTransactionsByMonthAndType(account.getId(), 
											criteria.getMonth(), criteria.getTransactionType());
			}
		}

		return doGetTransactions(account.getId());
	}
	
	
	private String getDefaultRestEndpoint() {
		return "http://localhost:" + CATALINA_PORT + "/parabank/services/bank";
	}
}