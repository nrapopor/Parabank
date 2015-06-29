package com.parasoft.parabank.service;

import java.math.BigDecimal;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.*;

import  org.apache.cxf.jaxrs.model.wadl.ElementClass;

import com.parasoft.parabank.domain.Account;
import com.parasoft.parabank.domain.Customer;
import com.parasoft.parabank.domain.LoanResponse;
import com.parasoft.parabank.domain.Position;
import com.parasoft.parabank.domain.HistoryPoint;
import com.parasoft.parabank.domain.Transaction;


/**
 * Java interface for ParaBank web service
 */
@Path("/")
@Produces({"application/xml", "application/json"})
@WebService(targetNamespace=ParaBankService.TNS)
public interface ParaBankService {
	    String TNS = "http://service.parabank.parasoft.com/";
    
    /**
     * Reset database contents to a populated state 
     */
    @POST
    @Path("/initializeDB")
    void initializeDB();
   
    /**
     * Reset database contents to a minimal state 
     */
    @POST
    @Path("/cleanDB")
    void cleanDB();
    
    /**
     * Enable JMS message listener 
     */
    @POST
    @Path("/startupJmsListener")
    void startupJmsListener();
    
    /**
     * Disable JMS message listener 
     */
    @POST
    @Path("/shutdownJmsListener")
    void shutdownJmsListener();
    
    /**
     * Sets the value of a given configuration parameter
     * 
     * @param name the name of the parameter
     * @param value the value to set
     */
    @GET
    @Path("/setParameter/{name}/{value}")
    void setParameter(
    	@PathParam("name")
        @WebParam(name="name", targetNamespace=TNS) String name,
        @PathParam("value")
        @WebParam(name="value", targetNamespace=TNS) String value
    );
    
    /**
     * Return the customer id for the given username and password
     * 
     * @param username the username to lookup
     * @param password the password for the customer
     * @return the customer id
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/login/{username}/{password}")
    @Produces({"application/xml","application/json"})   
    @WebResult(name="customerId", targetNamespace=TNS)
    @org.apache.cxf.jaxrs.model.wadl.ElementClass
    Customer login(
    	@PathParam("username")
    	@WebParam(name="username", targetNamespace=TNS) String username,
    	@PathParam("password")
        @WebParam(name="password", targetNamespace=TNS) String password
    ) throws ParaBankServiceException;
    
    /**
     * Return customer information for the given customer number
     * 
     * @param customerId the customer id to lookup
     * @return the customer
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/customers/{customerId}")
    @WebResult(name="customer", targetNamespace=TNS)
    Customer getCustomer(
        @PathParam("customerId")
        @WebParam(name="customerId", targetNamespace=TNS)
        int customerId
    ) throws ParaBankServiceException;
    
    /**
     * Return a list of accounts for a given customer
     * 
     * @param customerId the customer id to lookup
     * @return list of customer accounts
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/customers/{customerId}/accounts")
    @WebResult(name="account", targetNamespace=TNS)
    List<Account> getAccounts(
        @PathParam("customerId")
        @WebParam(name="customerId", targetNamespace=TNS) int customerId
    ) throws ParaBankServiceException;
    
    /**
     * Return account information for a given account number
     * 
     * @param accountId the account id to lookup
     * @return the account
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/accounts/{accountId}")
    @WebResult(name="account", targetNamespace=TNS)
    Account getAccount(
        @PathParam("accountId")
        @WebParam(name="accountId", targetNamespace=TNS)
        int accountId
    ) throws ParaBankServiceException;
    
    /**
     * Return a list of positions for a given customer
     * 
     * @param customerId the customer id to lookup
     * @return list of positions
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/customers/{customerId}/positions")
    @WebResult(name="position", targetNamespace=TNS)
    List<Position> getPositions(
        @PathParam("customerId")
        @WebParam(name="customerId", targetNamespace=TNS) int customerId
    ) throws ParaBankServiceException;
    
    /**
     * Return a position for a given position number
     * 
     * @param positionId the position id to lookup
     * @return the position
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/positions/{positionId}")
    @WebResult(name="position", targetNamespace=TNS)
    Position getPosition(
        @PathParam("positionId")
        @WebParam(name="positionId", targetNamespace=TNS)
        int positionId
    ) throws ParaBankServiceException;    
    
    /**
     * Return position history for a given position id
     * and date range
     * 
     * @param positionId the position id
     * @param startDate the start date in the date range
     * @param endDate the end date in the date range
     * @return a list of history points
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/positions/{positionId}/{startDate}/{endDate}")
    @WebResult(name="historyPoint", targetNamespace=TNS)
    List<HistoryPoint> getPositionHistory(
        @PathParam("positionId")
        @WebParam(name="positionId", targetNamespace=TNS) int positionId,
        @PathParam("startDate") @WebParam(name="startDate", targetNamespace=TNS) String startDate,
        @PathParam("endDate") @WebParam(name="endDate", targetNamespace=TNS) String endDate
    ) throws ParaBankServiceException;
    
    /**
     * Buy a position
     * 
     * @param customerId the customer id to purchase the position
     * @param accountId the account from which to withdraw funds
     * @param name the name of the stock position company
     * @param symbol the symbol of the stock position company
     * @param shares the number of shares to purchase
     * @param pricePerShare the price per share of the stock position
     * @return a list of positions for the customer
     * @throws ParaBankServiceException
     */
    @POST
    @Path("/customers/{customerId}/buyPosition")
    @WebResult(name="position", targetNamespace=TNS)
    List<Position> buyPosition(
        @PathParam("customerId")
        @WebParam(name="customerId", targetNamespace=TNS) int customerId,
        @QueryParam("accountId")
        @WebParam(name="accountId", targetNamespace=TNS) int accountId,
        @QueryParam("name")
        @WebParam(name="name", targetNamespace=TNS) String name,
        @QueryParam("symbol")
        @WebParam(name="symbol", targetNamespace=TNS) String symbol,
        @QueryParam("shares")
        @WebParam(name="shares", targetNamespace=TNS) int shares,
        @QueryParam("pricePerShare")
        @WebParam(name="pricePerShare", targetNamespace=TNS) BigDecimal pricePerShare
    ) throws ParaBankServiceException;
    
   /**
     * Sell a position
     * 
     * @param customerId the customer selling the position
     * @param accountId the account in which to deposit funds
     * @param positionId the position being sold
     * @param shares the number of shares to sell
     * @param pricePerShare the price per share of the stock position
     * @return a list of positions for the customer
     * @throws ParaBankServiceException
     */
    @POST
    @Path("/customers/{customerId}/sellPosition")
    @WebResult(name="position", targetNamespace=TNS)
    List<Position> sellPosition(
        @PathParam("customerId")
        @WebParam(name="customerId", targetNamespace=TNS) int customerId,
        @QueryParam("accountId")
        @WebParam(name="accountId", targetNamespace=TNS) int accountId,
        @QueryParam("positionId")
        @WebParam(name="positionId", targetNamespace=TNS) int positionId,
        @QueryParam("shares")
        @WebParam(name="shares", targetNamespace=TNS) int shares,
        @QueryParam("pricePerShare")
        @WebParam(name="pricePerShare", targetNamespace=TNS) BigDecimal pricePerShare
    ) throws ParaBankServiceException;
        
    /**
     * Return a list of transactions for a given account
     * 
     * @param accountId the account id to lookup
     * @return list of account transactions
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/accounts/{accountId}/transactions")
    @WebResult(name="transaction", targetNamespace=TNS)
    List <Transaction> getTransactions(
        @PathParam("accountId")
        @WebParam(name="accountId", targetNamespace=TNS) int accountId
    ) throws ParaBankServiceException;
    
    /**
     * Return transaction information for a given transaction id
     * 
     * @param transactionId the transaction id to lookup
     * @return the transaction
     * @throws ParaBankServiceException
     */
    @GET
    @Path("/transactions/{transactionId}")
    @WebResult(name="transaction", targetNamespace=TNS)
    @ElementClass(response = Transaction.class)
    Transaction getTransaction(
        @PathParam("transactionId")
        @WebParam(name="transactionId", targetNamespace=TNS) int transactionId
    ) throws ParaBankServiceException;
    
    /**
     * Deposit funds into the given account
     * 
     * @param accountId the account to which to deposit funds
     * @param amount the amount to deposit
     * @return status message of result
     * @throws ParaBankServiceException
     */
    @POST
    @Path("/deposit")
    @WebResult(name="depositReturn", targetNamespace=TNS)
    String deposit(
        @QueryParam("accountId")
        @WebParam(name="accountId", targetNamespace=TNS) int accountId,
        @QueryParam("amount")
        @WebParam(name="amount", targetNamespace=TNS) BigDecimal amount
    ) throws ParaBankServiceException;
    
    /**
     * Withdraw funds out of the given account
     * 
     * @param accountId the account from which to withdraw funds
     * @param amount the amount to withdraw
     * @return status message of result
     * @throws ParaBankServiceException
     */
    @POST
    @Path("/withdraw")
    @WebResult(name="withdrawReturn", targetNamespace=TNS)
    String withdraw(
        @QueryParam("accountId")
        @WebParam(name="accountId", targetNamespace=TNS) int accountId,
        @QueryParam("amount")
        @WebParam(name="amount", targetNamespace=TNS) BigDecimal amount
    ) throws ParaBankServiceException;
    
    /**
     * Transfer funds between two accounts
     * 
     * @param fromAccountId the account from which to withdraw funds
     * @param toAccountId the account to which to deposit funds
     * @param amount the amount to transfer
     * @return status message of result
     * @throws ParaBankServiceException
     */
    @POST
    @Path("/transfer")
    @WebResult(name="transferReturn", targetNamespace=TNS)
    String transfer(
        @QueryParam("fromAccountId")
        @WebParam(name="fromAccountId", targetNamespace=TNS) int fromAccountId,
        @QueryParam("toAccountId")
        @WebParam(name="toAccountId", targetNamespace=TNS) int toAccountId,
        @QueryParam("amount")
        @WebParam(name="amount", targetNamespace=TNS) BigDecimal amount
    ) throws ParaBankServiceException;
    
    /**
     * Request a loan
     * 
     * @param customerId the customer requesting a loan
     * @param amount the amount of the loan
     * @param downPayment the down payment for the loan
     * @param fromAccountId the account from which to deduct the down payment 
     * @return response the result of the loan request
     */
    @POST
    @Path("/requestLoan")
    @WebResult(name="loanResponse", targetNamespace=TNS)
    LoanResponse requestLoan(
        @QueryParam("customerId")
        @WebParam(name="customerId", targetNamespace=TNS) int customerId,
        @QueryParam("amount")
        @WebParam(name="amount", targetNamespace=TNS) BigDecimal amount,
        @QueryParam("downPayment")
        @WebParam(name="downPayment", targetNamespace=TNS) BigDecimal downPayment,
        @QueryParam("fromAccountId")
        @WebParam(name="fromAccountId", targetNamespace=TNS) int fromAccountId
    ) throws ParaBankServiceException;
  
    
    @POST
    @Path("/createAccount")
    @WebResult(name="account", targetNamespace=TNS)
    public Account createAccount(
    	@QueryParam("customerId")
    	@WebParam(name="customerId", targetNamespace=TNS) int customerId, 
    	@QueryParam("newAccountType")
        @WebParam(name="newAccountType", targetNamespace=TNS) int newAccountType,	
    	@QueryParam("fromAccountId")
        @WebParam(name="fromAccountId", targetNamespace=TNS) int fromAccountId
    ) throws ParaBankServiceException;
    
    
    @GET
    @Path("/accounts/{accountId}/transactions/onDate/{onDate}")
    @WebResult(name="transaction", targetNamespace=TNS)
    public List <Transaction> getTransactionsOnDate(
            @PathParam("accountId")
            @WebParam(name="accountId", targetNamespace=TNS) int accountId,
            @PathParam("onDate")
            @WebParam(name="onDate", targetNamespace=TNS) String onDate
    ) throws ParaBankServiceException;
    
    
    @GET
    @Path("/accounts/{accountId}/transactions/fromDate/{fromDate}/toDate/{toDate}")
    @WebResult(name="transaction", targetNamespace=TNS)
    public List <Transaction> getTransactionsByToFromDate(
            @PathParam("accountId")
            @WebParam(name="accountId", targetNamespace=TNS) int accountId,
            @PathParam("fromDate")
            @WebParam(name="fromDate", targetNamespace=TNS) String fromDate,
            @PathParam("toDate")
            @WebParam(name="toDate", targetNamespace=TNS) String toDate
    ) throws ParaBankServiceException;
    
    
    @GET
    @Path("/accounts/{accountId}/transactions/amount/{amount}")
    @WebResult(name="transaction", targetNamespace=TNS)
    public List <Transaction> getTransactionsByAmount(
            @PathParam("accountId")
            @WebParam(name="accountId", targetNamespace=TNS) int accountId,
            @PathParam("amount")
            @WebParam(name="amount", targetNamespace=TNS) BigDecimal amount
    ) throws ParaBankServiceException;
    
    
    @GET
    @Path("/accounts/{accountId}/transactions/month/{month}/type/{type}")
    @WebResult(name="transaction", targetNamespace=TNS)
    public List<Transaction> getTransactionsByMonthAndType(
    		@PathParam("accountId")
    		@WebParam(name="accountId", targetNamespace=TNS) int accountId,
    		@PathParam("month")
    		@WebParam(name="month", targetNamespace=TNS) String month,
    		@PathParam("type")
    		@WebParam(name="type", targetNamespace=TNS) String type
    ) throws ParaBankServiceException;
    
    
    @POST
    @Path("/customers/update/{customerId}/{firstName}/{lastName}/{street}/{city}/{state}/{zipCode}/{phoneNumber}/{ssn}/{username}/{password}")
    @WebResult(name="customerUpdateResult", targetNamespace=TNS)
    public String updateCustomer(@PathParam("customerId")
    							 @WebParam(name="customerId", targetNamespace=TNS) int customerId, 
    							 @PathParam("firstName")
    							 @WebParam(name="firstName", targetNamespace=TNS) String firstName, 
    							 @PathParam("lastName")
    							 @WebParam(name="lastName", targetNamespace=TNS) String lastName, 
    							 @PathParam("street")
    							 @WebParam(name="street", targetNamespace=TNS) String street, 
    							 @PathParam("city")
    							 @WebParam(name="city", targetNamespace=TNS) String city, 
    							 @PathParam("state")
    							 @WebParam(name="state", targetNamespace=TNS) String state, 
    							 @PathParam("zipCode")
    							 @WebParam(name="zipCode", targetNamespace=TNS) String zipCode, 
    							 @PathParam("phoneNumber")
    							 @WebParam(name="phoneNumber", targetNamespace=TNS) String phoneNumber,
    							 @PathParam("ssn")
    							 @WebParam(name="ssn", targetNamespace=TNS) String ssn,
    							 @PathParam("username")
    							 @WebParam(name="username", targetNamespace=TNS) String username,
    							 @PathParam("password")
    							 @WebParam(name="password", targetNamespace=TNS) String password)
    							 throws ParaBankServiceException;
}
